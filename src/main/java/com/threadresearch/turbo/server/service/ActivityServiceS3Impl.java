package com.threadresearch.turbo.studyconfigurator.server.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.amazonaws.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.threadresearch.turbo.studyconfigurator.server.config.Constants;
import com.threadresearch.turbo.studyconfigurator.server.domain.ClientActivity;
import com.threadresearch.turbo.studyconfigurator.server.domain.Language;
import com.threadresearch.turbo.studyconfigurator.server.domain.LocalizedResources;
import com.threadresearch.turbo.studyconfigurator.server.domain.Schedule;
import com.threadresearch.turbo.studyconfigurator.server.domain.ScheduleTask;
import com.threadresearch.turbo.studyconfigurator.server.domain.ScheduleWrapper;
import com.threadresearch.turbo.studyconfigurator.server.domain.StudyActivityMetaData;
import com.threadresearch.turbo.studyconfigurator.server.domain.StudyFeature;
import com.threadresearch.turbo.studyconfigurator.server.domain.StudyMetaData;
import com.threadresearch.turbo.studyconfigurator.server.domain.TaskSchedulesOptions;
import com.threadresearch.turbo.studyconfigurator.server.domain.User;
import com.threadresearch.turbo.studyconfigurator.server.dto.ActivityConfigurationSetting;
import com.threadresearch.turbo.studyconfigurator.server.dto.ActivityDTO;
import com.threadresearch.turbo.studyconfigurator.server.dto.ActivityDTOWrapper;
import com.threadresearch.turbo.studyconfigurator.server.dto.DurationDTO;
import com.threadresearch.turbo.studyconfigurator.server.dto.ScheduleDTO;
import com.threadresearch.turbo.studyconfigurator.server.dto.TaskSchedulesOptionalDTO;
import com.threadresearch.turbo.studyconfigurator.server.enums.ActivitySchedulingModes;
import com.threadresearch.turbo.studyconfigurator.server.enums.ScheduleType;
import com.threadresearch.turbo.studyconfigurator.server.loghandler.Log;
import com.threadresearch.turbo.studyconfigurator.server.repository.ClientActivityRepository;
import com.threadresearch.turbo.studyconfigurator.server.repository.LanguageRepository;
import com.threadresearch.turbo.studyconfigurator.server.repository.LocalizedResourcesRepository;
import com.threadresearch.turbo.studyconfigurator.server.repository.ParticipantOptionalTaskIdsRepository;
import com.threadresearch.turbo.studyconfigurator.server.repository.StudyActivityMetaDataRepository;
import com.threadresearch.turbo.studyconfigurator.server.repository.StudyFeatureRepository;
import com.threadresearch.turbo.studyconfigurator.server.repository.StudyMetaDataRepository;
import com.threadresearch.turbo.studyconfigurator.server.security.UserAuthentication;
import com.threadresearch.turbo.studyconfigurator.server.utils.MimeTypes;
import com.threadresearch.turbo.studyconfigurator.server.utils.TimeUtilities;

/**
 * . Activity Service Implementation Class
 * 
 * @author Abhishek
 *
 */
@Service
public class ActivityServiceS3Impl implements ActivityService {

	@Autowired
	SchedulerServiceImpl scheduleServiceImpl;

	@Autowired
	private StorageService storageService;

	@Autowired
	SchedulerService schedulerService;

	@Autowired
	private ClientActivityRepository clientActivityRepository;

	@Autowired
	StudyService studyService;

	@Value("${studyActivitiesListJsonFileName}")
	private String studyActivitiesListJsonFileName;

	@Value("${s3.studies.folder}")
	private String studies;

	@Value("${s3.activities.folder}")
	private String activities;

	@Value("${schedulerFileName}")
	private String schedulerFileName;

	@Value("${surveybuilder.url}")
	private String surveyConfiguratorUrl;

	private static String CLIENT_HEADER_KEY = "clientId";

	@Log
	private Logger logger;

	@Autowired
	LocalizedResourcesRepository localizedResourcesRepository;

	@Autowired
	LanguageRepository languageRepository;

	@Autowired
	ParticipantOptionalTaskIdsRepository participantOptionalTaskIdsRepository;

	/**
	 * . Adds study activity info
	 * 
	 * @param studyId
	 * @param activityId
	 * @param activityDTO
	 * @throws Exception
	 */
	@Override
	public ActivityDTO updateStudyActivitySchedule(String studyId, String activityId, ActivityDTO activityDTO,
			String clientId, User user) throws Exception {
		logger.info("activity id===>" + activityId);
		updateActivityScheduleInfoInS3(studyId, activityId, activityDTO, clientId);
		updateActivityMetaDataInRDBMS(studyId, activityId, activityDTO, clientId,
				user);
		if (activityDTO.getScheduler() != null) {
			// ScheduleDTO scheduleDTO = activityDTO.getScheduler();
			activityDTO.getScheduler().setTaskTitle(activityDTO.getTitle());
			activityDTO.getScheduler().setTaskKey(activityDTO.getKey());
			Schedule schedule = schedulerService.addStudySchedule(studyId,
					activityDTO.getScheduler(), clientId);
			if ((!activityDTO.getScheduler().getActivitySchedulingMode()
					.equalsIgnoreCase(ActivitySchedulingModes.OnDemand.getName())
					&& !activityDTO.getScheduler().getActivitySchedulingMode()
							.equalsIgnoreCase(ActivitySchedulingModes.DynamicOnly.getName()))
					&&
					schedule.getScheduleType().equalsIgnoreCase(ScheduleType.CUSTOM.getName())) {
				if (schedule.getDuration() != null && !schedule.getDuration().isEmpty()) {
					if (schedule.isAllowUntillDuration()) {
						activityDTO.getScheduler().getDuration().getLast().setEndDay(null);
					}
				}
			} else if ((!activityDTO.getScheduler().getActivitySchedulingMode()
					.equalsIgnoreCase(ActivitySchedulingModes.OnDemand.getName())
					&& !activityDTO.getScheduler().getActivitySchedulingMode()
							.equalsIgnoreCase(ActivitySchedulingModes.DynamicOnly.getName()))
					&&
					schedule.getScheduleType().equalsIgnoreCase(ScheduleType.MILESTONE.getName())) {
				LinkedList<DurationDTO> duration = new LinkedList<DurationDTO>();
				duration.add(new DurationDTO());
				activityDTO.getScheduler().setDuration(duration);
			}
		}
		return activityDTO;
	}

	/**
	 * . Validate Activity Wrapper object
	 * 
	 * @param activityDTOWrapper
	 * @return isValid
	 */
	@Override
	public boolean validateActivityWrapper(ActivityDTOWrapper activityDTOWrapper, String flag) {
		boolean isValid = true;
		List<ActivityDTO> activitiesList = activityDTOWrapper.getActivitiesList();
		for (ActivityDTO activityDTO : activitiesList) {
			if (!validateActivityDTO(activityDTO, flag, null)) {
				isValid = false;
				return isValid;
			}
		}
		return isValid;
	}

	/**
	 * . Saves Study activity list in S3
	 * 
	 * @param studyId
	 * @param activityDTOWrapper
	 * @throws Exception
	 */
	@Override
	public ActivityDTOWrapper saveStudyActivitiesList(String studyId,
			ActivityDTOWrapper activityDTOWrapper,
			HttpServletRequest httpServletRequest) throws Exception {
		StudyMetaData studyMetaData = studyMetaDataRepository.findOne(studyId);
		if (studyMetaData == null) {
			throw new Exception("StudyId doesn't exist in system");
		}
		logger.info("Activities size is===>" +
				activityDTOWrapper.getActivitiesList().size());
		updateFieldsInActivityDTOWrapper(activityDTOWrapper, "create");
		String s3Path = studies + "/" + studyId;
		String filePath = null;
		ActivityDTOWrapper updatedActivityDTOWrapper = new ActivityDTOWrapper();
		updatedActivityDTOWrapper.setActivitiesList(new LinkedList<ActivityDTO>());
		List<String> newActivitiesKeys = new ArrayList<String>();
		List<String> newActivitiesKeysForeDRO = new ArrayList<String>();
		StudyActivityMetaData metaData = null;
		User user = ((UserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getDetails();
		logger.info("User from authorization token===>" + user.getUsername());
		int order = 1;
		for (ActivityDTO activityDTO : activityDTOWrapper.getActivitiesList()) {
			newActivitiesKeys.add(activityDTO.getKey());
			if (activityDTO.getKey().equals(Constants.FEATURE_KEY_FORCED_SPIROMETER)) {
				newActivitiesKeysForeDRO.add(Constants.FEATURE_KEY_EDRO_DASHBOARD);
			}

			metaData = getStudyActivityMetaData(studyId, activityDTO);
			if (metaData == null) {
				updatedActivityDTOWrapper.getActivitiesList().add(activityDTO);
				logger.debug(
						"Activity id===>" + activityDTO.getIdentifier() + "Activity name===>" +
								activityDTO.getKey());
				filePath = s3Path + "/" + activities + "/" + activityDTO.getIdentifier() +
						".json";
				logger.debug("Activity s3 storage path===>" + filePath);
				activityDTO.setS3Path(filePath);
				storageService.saveObject(activityDTO, filePath, "application/json",
						httpServletRequest.getHeader(CLIENT_HEADER_KEY));
				// setDefaultScheduleInfo(activityDTO);
				if (activityDTO.getScheduler() != null) {
					schedulerService.addStudySchedule(studyId, activityDTO.getScheduler(),
							httpServletRequest.getHeader(CLIENT_HEADER_KEY));
				}
				saveStudyActivityMetaData(studyId, user, order, activityDTO,
						httpServletRequest.getHeader(CLIENT_HEADER_KEY));
			} else {
				try {
					logger.info("**********updating metadata {saveStudyActivitiesList} :");
					updateStudyActivityMetadata(studyId, metaData.getActivityKey(), order);
					/** UPDATING STUDY FEATURE TABLE ONLY FOR EDRO_DASHBOARD **/
					if (activityDTO.getKey().equals(Constants.FEATURE_KEY_FORCED_SPIROMETER)) {
						logger.info("Creating EdroDashboardDTO...");
						ActivityDTO edroDashboardDTO = new ActivityDTO();
						edroDashboardDTO.setDescription(Constants.FEATURE_DESCRIPTION_EDRO_DASHBOARD);
						edroDashboardDTO.setKey(Constants.FEATURE_KEY_EDRO_DASHBOARD);
						edroDashboardDTO.setSelected(activityDTO.getSelected());
						edroDashboardDTO.setTitle(Constants.FEATURE_KEY_EDRO_DASHBOARD);
						updateEdroDashboardForStudyFeature(studyId, edroDashboardDTO);

						logger.info("**********inside else condition end {saveStudyActivitiesList}");
					}
				} catch (Exception ex) {
					logger.error("*****{saveStudyActivitiesList} in exception block" +
							ex.getMessage(), ex);
					throw new Exception("exception while saving studyFeatureRepository : " +
							ex.getMessage());
				}
			}
			order++;
		}
		deleteUnSelectedActivities(studyId, newActivitiesKeys,
				httpServletRequest.getHeader(CLIENT_HEADER_KEY), newActivitiesKeysForeDRO);
		// add activities to dashboard by making rest call to surveys
		studyService.addActivitiesToAdherenceDashboard(studyId,
				httpServletRequest.getHeader(CLIENT_HEADER_KEY),
				activityDTOWrapper.getActivitiesList(), httpServletRequest);
		return activityDTOWrapper;
	}

	/**
	 * . Sets the default Scheduler to activity object
	 * 
	 * @param activityDTO
	 */
	private void setDefaultScheduleInfo(ActivityDTO activityDTO) {
		ScheduleDTO scheduleDTO = new ScheduleDTO();
		scheduleDTO.setType("once");
		scheduleDTO.setTaskId(activityDTO.getIdentifier());
		scheduleDTO.setTaskType("activity");
		scheduleDTO.setTaskTitle(activityDTO.getTitle());
		scheduleDTO.setTaskKey(activityDTO.getKey());
		activityDTO.setScheduler(scheduleDTO);
	}

	/**
	 * . Delete the unSelected activity from study feature table
	 * 
	 * @param studyId
	 * @param activityKey
	 */
	private void deleteUnSelectedActivityFromStudyFeature(String studyId, String activityKey) {
		logger.debug("Deleted activity key from study feature table===>" + activityKey);
		studyFeatureRepository.deleteByStudyIdAndActivityKey(studyId, activityKey);
	}

	/**
	 * . Get activity meta data by studyId and activityKey
	 * 
	 * @param studyId
	 * @param activityDTO
	 * @return StudyActivityMetaData
	 */
	private StudyActivityMetaData getStudyActivityMetaData(String studyId, ActivityDTO activityDTO) {
		return studyActivityMetaDataRepository.findByStudyIdAndActivityKey(studyId, activityDTO.getKey());
	}

	/**
	 * . Updates activity dto wrapper fields
	 * 
	 * @param activityDTOWrapper
	 * @param flag
	 */
	private void updateFieldsInActivityDTOWrapper(ActivityDTOWrapper activityDTOWrapper, String flag) {
		if ("create".equals(flag)) {
			for (ActivityDTO activityDTO : activityDTOWrapper.getActivitiesList()) {
				if (activityDTO.getIdentifier() == null) {
					activityDTO.setIdentifier(UUID.randomUUID().toString());
				}
			}
		}
	}

	/**
	 * . Get the list of Study activities
	 * 
	 * @param studyId
	 * @return ActivityDTOWrapper
	 * @throws Exception
	 */
	@Override
	public ActivityDTOWrapper getStudyActivities(String studyId, String clientId)
			throws Exception {
		StudyMetaData studyMetaData = studyMetaDataRepository.findOne(studyId);
		if (studyMetaData == null) {
			throw new Exception("StudyId doesn't exist in system");
		}

		List<StudyActivityMetaData> listActivity = studyActivityMetaDataRepository
				.findByStudyIdOrderByOrderingAsc(studyId);
		ActivityDTOWrapper response = new ActivityDTOWrapper();

		if (listActivity != null && listActivity.size() > 0) {
			response = storageService.getStudyActivities(listActivity, clientId);
		}

		return response;
	}

	/**
	 * . This method deletes study activity
	 * 
	 * @param studyId
	 * @param activityId
	 * @return true/false
	 * @throws Exception
	 */
	@Override
	public Boolean deleteStudyActivity(String studyId, String activityId, String clientId) throws Exception {
		StudyActivityMetaData studyActivityMetaData = studyActivityMetaDataRepository.findOne(activityId);
		if (studyActivityMetaData == null) {
			throw new Exception("invalid activityId");
		}
		deleteUnSelectedActivity(studyId, studyActivityMetaData.getActivityKey(),
				studyActivityMetaData.getS3ObjectKey(), clientId);
		// delete corresponding dashboard activity
		deleteAdherenceDashboardActivity(studyId, clientId, activityId);
		updateActivitiesSchedulerInfo(studyId, new ArrayList<String>() {
			{
				add(activityId);
			}
		}, clientId);
		return true;
	}

}

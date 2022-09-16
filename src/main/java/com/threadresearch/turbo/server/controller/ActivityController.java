package com.threadresearch.turbo.studyconfigurator.server.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.threadresearch.turbo.studyconfigurator.server.config.EndPointConfig;
import com.threadresearch.turbo.studyconfigurator.server.domain.User;
import com.threadresearch.turbo.studyconfigurator.server.dto.ActivityDTO;
import com.threadresearch.turbo.studyconfigurator.server.dto.ActivityDTOWrapper;
import com.threadresearch.turbo.studyconfigurator.server.dto.SiteDTO;
import com.threadresearch.turbo.studyconfigurator.server.dto.TaskSchedulesOptionalDTO;
import com.threadresearch.turbo.studyconfigurator.server.enums.ActivitySchedulingModes;
import com.threadresearch.turbo.studyconfigurator.server.security.UserAuthentication;
import com.threadresearch.turbo.studyconfigurator.server.service.ActivityService;
import com.threadresearch.turbo.studyconfigurator.server.service.SchedulerServiceImpl;
import com.threadresearch.turbo.studyconfigurator.server.utils.ValidateParameter;

import io.swagger.annotations.ApiOperation;

/**
 * . Activity Controller
 * 
 * @author Abhishek Hande
 *
 */
@RestController
@RequestMapping(value = EndPointConfig.VERSION_1 + EndPointConfig.CONFIGURATOR)
public class ActivityController {

    @Autowired
    ActivityService activityService;

    @Autowired
    SchedulerServiceImpl schedulerService;

    private static String CLIENT_HEADER_KEY = "clientId";

    /**
     * . Save Study Activity list in S3.
     * 
     * @param studyId
     * @param activityId
     * @param activityDTO
     * @return Uploaded Activity list
     * @throws Exception when required fields are not present in input request json
     */
    @ApiOperation(value = "Study Activities List", notes = "Add study activities list in S3")
    @RequestMapping(value = EndPointConfig.ACTIVITIES, method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('SystemAdmin','StudyAdmin')")
    public ResponseEntity<ActivityDTOWrapper> addStudyActivitiesList(@PathVariable(value = "id") String studyId,
            HttpServletRequest httpServletRequest,
            @RequestBody ActivityDTOWrapper activityDTOWrapper)
            throws Exception {
        ValidateParameter.checkForClientId(httpServletRequest);
        if (activityDTOWrapper == null) {
            throw new Exception("activityDTOWrapper input json is required");
        } else if (activityDTOWrapper.getActivitiesList() == null) {
            throw new Exception("Need activities list info in input activityDTOWrapper json");
        } else if (!activityService.validateActivityWrapper(activityDTOWrapper, "create")) {
            throw new Exception("Invalid Input activityDTOWrapper object");
        }
        MDC.put("studyId", studyId);
        ActivityDTOWrapper result = activityService.saveStudyActivitiesList(studyId,
                activityDTOWrapper, httpServletRequest);
        return new ResponseEntity<ActivityDTOWrapper>(result, HttpStatus.OK);
    }

    /**
     * . Gets Study Activities list json from S3
     * 
     * @param studyId
     * @return Study Activities list Json
     * @throws Exception if study does not exist with given study id
     */
    @ApiOperation(value = "Get Study Activities list", notes = "Gets Study Activities list from S3")
    @RequestMapping(value = EndPointConfig.ACTIVITIES, method = RequestMethod.GET)
    @PreAuthorize("hasAnyAuthority('SystemAdmin','StudyAdmin','SiteDataLock', 'Pi','Quality','SubPi','StudyCoordinator','HomeHealth', 'CallCenter', 'Rater', 'Interviewer', 'Cra','Dm')")
    public ResponseEntity<List<ActivityDTO>> getStudyActivities(@PathVariable(value = "id") String studyId,
            HttpServletRequest httpServletRequest) throws Exception {
        ValidateParameter.checkForClientId(httpServletRequest);
        MDC.put("studyId", studyId);
        ActivityDTOWrapper activityDTOWrapper = activityService.getStudyActivities(studyId,
                httpServletRequest.getHeader(CLIENT_HEADER_KEY));
        return new ResponseEntity<List<ActivityDTO>>(activityDTOWrapper.getActivitiesList(), HttpStatus.OK);
    }

    /**
     * . Update study activity schedule info
     * 
     * @param studyId
     * @param activityId
     * @param activityDTO
     * @return Updated Study Activity json
     * @throws Exception when required fields are not present in input request
     */
    @ApiOperation(value = "Study Activity Schedules", notes = "Add & update study activity schedules")
    @RequestMapping(value = EndPointConfig.ACTIVITIES_ACTIVITYID, method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('SystemAdmin','StudyAdmin')")
    public ResponseEntity<ActivityDTO> updateStudyActivitySchedule(@PathVariable(value = "id") String studyId,
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "activity_id") String activityId, @RequestBody ActivityDTO activityDTO)
            throws Exception {
        ValidateParameter.checkForClientId(httpServletRequest);
        if (activityDTO == null) {
            throw new Exception("Activity DTO input json is required");
        } else if ((!activityDTO.getScheduler().getActivitySchedulingMode()
                .equalsIgnoreCase(ActivitySchedulingModes.OnDemand.getName())
                && !activityDTO.getScheduler().getActivitySchedulingMode()
                        .equalsIgnoreCase(ActivitySchedulingModes.DynamicOnly.getName()))
                && !activityService.validateActivityDTO(activityDTO, "update", activityId)) {
            throw new Exception("Invalid Input activity DTO object");
        }
        MDC.put("studyId", studyId);
        User user = ((UserAuthentication) SecurityContextHolder.getContext().getAuthentication()).getDetails();
        ActivityDTO result = activityService.updateStudyActivitySchedule(studyId, activityId,
                activityDTO, httpServletRequest.getHeader(CLIENT_HEADER_KEY), user);
        return new ResponseEntity<ActivityDTO>(result, HttpStatus.OK);
    }

    /**
     * . Delete Study Activity
     * 
     * @param studyId
     * @param activityId
     * @return true/false
     * @throws Exception when required fields are not present in input request
     */
    @ApiOperation(value = "Study Activity Schedules", notes = "Delete Study Activities")
    @RequestMapping(value = EndPointConfig.ACTIVITIES_ACTIVITYID, method = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('SystemAdmin','StudyAdmin')")
    public ResponseEntity<Boolean> deleteStudyActivity(@PathVariable(value = "id") String studyId,
            HttpServletRequest httpServletRequest,
            @PathVariable(value = "activity_id") String activityId) throws Exception {
        ValidateParameter.checkForClientId(httpServletRequest);
        if (studyId == null && activityId == null) {
            throw new Exception("some of the Input params are missing");
        }
        MDC.put("studyId", studyId);
        Boolean result = activityService.deleteStudyActivity(studyId, activityId,
                httpServletRequest.getHeader(CLIENT_HEADER_KEY));
        return new ResponseEntity<Boolean>(result, HttpStatus.OK);
    }

}

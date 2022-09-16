package com.threadresearch.turbo.studyconfigurator.server.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import com.threadresearch.turbo.studyconfigurator.server.domain.User;
import com.threadresearch.turbo.studyconfigurator.server.dto.ActivityDTO;
import com.threadresearch.turbo.studyconfigurator.server.dto.ActivityDTOWrapper;
import com.threadresearch.turbo.studyconfigurator.server.dto.TaskSchedulesOptionalDTO;

/**
 * .
 * Activity Service Interface
 * 
 * @author Abhishek hande
 *
 */
public interface ActivityService {

        /**
         * .
         * Saves study activities list.
         * 
         * @param studyId
         * @param activityDTOWrapper
         * @return ActivityDTOWrapper
         * @throws Exception
         */
        ActivityDTOWrapper saveStudyActivitiesList(String studyId, ActivityDTOWrapper activityDTOWrapper,
                        HttpServletRequest httpServletRequest)
                        throws Exception;

        /**
         * .
         * Validates study activity dto wrapper
         * 
         * @param activityDTOWrapper
         * @param flag
         * @return true/false
         */
        boolean validateActivityWrapper(ActivityDTOWrapper activityDTOWrapper, String flag);

        /**
         * .
         * Gets the study activities list.
         * 
         * @param studyId
         * @return ActivityDTOWrapper
         * @throws Exception
         */
        ActivityDTOWrapper getStudyActivities(String studyId, String clientId) throws Exception;

        /**
         * .
         * Update study activity schedule info.
         * 
         * @param studyId
         * @param activityId
         * @param activityDTO
         * @return
         * @throws Exception
         */
        ActivityDTO updateStudyActivitySchedule(String studyId, String activityId,
                        ActivityDTO activityDTO, String clientId, User user) throws Exception;

        /**
         * .
         * This method deletes study activity
         * 
         * @param studyId
         * @param activityId
         * @return true/false
         * @throws Exception
         */
        Boolean deleteStudyActivity(String studyId, String activityId, String clientId) throws Exception;

}

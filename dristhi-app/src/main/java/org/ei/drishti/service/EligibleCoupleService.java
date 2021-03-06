package org.ei.drishti.service;

import org.ei.drishti.AllConstants;
import org.ei.drishti.domain.TimelineEvent;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.repository.AllBeneficiaries;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllTimelineEvents;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ei.drishti.AllConstants.NEW_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.domain.TimelineEvent.forChangeOfFPMethod;
import static org.ei.drishti.util.EasyMap.mapOf;

public class EligibleCoupleService {
    private final AllEligibleCouples allEligibleCouples;
    private final AllTimelineEvents allTimelineEvents;
    private final AllBeneficiaries allBeneficiaries;

    public EligibleCoupleService(AllEligibleCouples allEligibleCouples, AllTimelineEvents allTimelineEvents, AllBeneficiaries allBeneficiaries) {
        this.allEligibleCouples = allEligibleCouples;
        this.allTimelineEvents = allTimelineEvents;
        this.allBeneficiaries = allBeneficiaries;
    }

    public void register(FormSubmission submission) {
        if (isNotBlank(submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE))) {
            allTimelineEvents.add(TimelineEvent.forECRegistered(submission.entityId(), submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE)));
        }
    }

    public void fpComplications(FormSubmission submission) {
    }

    public void fpChange(FormSubmission submission) {
        String fpMethodChangeDate = submission.getFieldValue(AllConstants.ECRegistrationFields.FAMILY_PLANNING_METHOD_CHANGE_DATE);
        if (isBlank(fpMethodChangeDate)) {
            fpMethodChangeDate = submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE);
        }
        allTimelineEvents.add(forChangeOfFPMethod(submission.entityId(), submission.getFieldValue(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD),
                submission.getFieldValue(NEW_FP_METHOD_FIELD_NAME), fpMethodChangeDate));
        allEligibleCouples.mergeDetails(submission.entityId(), mapOf(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD, submission.getFieldValue(NEW_FP_METHOD_FIELD_NAME)));
    }

    public void renewFPProduct(FormSubmission submission) {
    }

    public void closeEligibleCouple(FormSubmission submission) {
        allEligibleCouples.close(submission.entityId());
        allBeneficiaries.closeAllMothersForEC(submission.entityId());
    }
}

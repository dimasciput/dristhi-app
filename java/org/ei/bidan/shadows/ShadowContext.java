package org.ei.bidan.shadows;

import org.ei.bidan.Context;
import org.ei.bidan.repository.AllBeneficiaries;
import org.ei.bidan.repository.AllEligibleCouples;
import org.ei.bidan.repository.Repository;
import org.ei.bidan.service.PendingFormSubmissionService;
import org.ei.bidan.view.controller.ANMController;
import org.ei.bidan.view.controller.ANMLocationController;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import static org.mockito.Mockito.when;

@Implements(Context.class)
public class ShadowContext {

    @Mock
    Repository repository;
    @Mock
    ANMController anmController;
    @Mock
    AllEligibleCouples allEligibleCouples;
    @Mock
    AllBeneficiaries allBeneficiaries;
    @Mock
    ANMLocationController anmLocationController;
    @Mock
    private PendingFormSubmissionService pendingFormSubmissionService;

    @Implementation
    public Boolean IsUserLoggedOut() {
        return false;
    }

    @Implementation
    public ANMController anmController() {
        return anmController;
    }

    @Implementation
    public AllEligibleCouples allEligibleCouples() {
        return allEligibleCouples;
    }

    @Implementation
    public AllBeneficiaries allBeneficiaries() {
        return allBeneficiaries;
    }

    @Implementation
    public ANMLocationController anmLocationController() {
        anmLocationController = Mockito.mock(ANMLocationController.class);
        when(anmLocationController.getLocationJSON()).thenReturn("");
        return anmLocationController;
    }

    public PendingFormSubmissionService pendingFormSubmissionService() {
        pendingFormSubmissionService = Mockito.mock(PendingFormSubmissionService.class);
        when(pendingFormSubmissionService.pendingFormSubmissionCount()).thenReturn(0l);
        return pendingFormSubmissionService;
    }
}

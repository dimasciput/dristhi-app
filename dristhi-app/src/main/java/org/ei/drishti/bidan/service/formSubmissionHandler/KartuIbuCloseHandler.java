package org.ei.drishti.bidan.service.formSubmissionHandler;

import org.ei.drishti.bidan.service.KartuIbuService;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */
public class KartuIbuCloseHandler implements FormSubmissionHandler {
    private KartuIbuService kartuIbuService;

    public KartuIbuCloseHandler(KartuIbuService kartuIbuService) {
        this.kartuIbuService = kartuIbuService;
    }

    @Override
    public void handle(FormSubmission submission) {
        kartuIbuService.closeKartuIbu(submission);
    }
}
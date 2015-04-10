package org.ei.bidan.view.dialog;

import org.ei.bidan.R;
import org.ei.bidan.provider.SmartRegisterClientsProvider;

import static org.ei.bidan.Context.getInstance;

public class FPPrioritizationOneChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationOneChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_one_child_service_mode);
    }
}
package org.ei.bidan.view.dialog;

import org.ei.bidan.Context;
import org.ei.bidan.R;
import org.ei.bidan.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.ei.bidan.view.contract.ANCSmartRegisterClient.EDD_COMPARATOR;

public class EstimatedDateOfDeliverySort implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_edd_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, EDD_COMPARATOR);
        return allClients;
    }
}

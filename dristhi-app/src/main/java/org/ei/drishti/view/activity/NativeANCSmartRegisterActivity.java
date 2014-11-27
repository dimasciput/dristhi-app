package org.ei.drishti.view.activity;

import android.view.View;
import org.ei.drishti.AllConstants;
import org.ei.drishti.R;
import org.ei.drishti.adapter.SmartRegisterPaginatedAdapter;
import org.ei.drishti.domain.form.FieldOverrides;
import org.ei.drishti.provider.ANCSmartRegisterClientsProvider;
import org.ei.drishti.provider.SmartRegisterClientsProvider;
import org.ei.drishti.view.contract.SmartRegisterClient;
import org.ei.drishti.view.controller.ANCSmartRegisterController;
import org.ei.drishti.view.controller.VillageController;
import org.ei.drishti.view.dialog.*;

import java.util.List;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static java.util.Arrays.asList;
import static org.ei.drishti.AllConstants.FormNames.*;

public class NativeANCSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private SmartRegisterClientsProvider clientProvider = null;
    private ANCSmartRegisterController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;
    public static final List<? extends DialogOption> DEFAULT_ANC_FILTER_OPTIONS =
            asList(new OutOfAreaFilter());

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new ANCSmartRegisterClientsProvider(
                    this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new ANCOverviewServiceMode(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();
            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.anc_label);
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                Iterable<? extends DialogOption> villageFilterOptions =
                        dialogOptionMapper.mapToVillageFilterOptions(villageController.getVillages());
                return toArray(concat(DEFAULT_FILTER_OPTIONS, DEFAULT_ANC_FILTER_OPTIONS, villageFilterOptions), DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                        new ANCOverviewServiceMode(clientsProvider()),
                        new ANCVisitsServiceMode(clientsProvider()),
                        new TTServiceMode(clientsProvider())
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new EstimatedDateOfDeliverySort(),
                        new HighPrioritySort(), new BPLSort(),
                        new SCSort(), new STSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_anc_search_hint);
            }
        };
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_anc_visit_form), ANC_VISIT, formController),
                new OpenFormOption(getString(R.string.str_register_hb_test_form), HB_TEST, formController),
                new OpenFormOption(getString(R.string.str_register_ifa_form), IFA, formController),
                new OpenFormOption(getString(R.string.str_register_tt_form), TT, formController),
                new OpenFormOption(getString(R.string.str_register_delivery_plan_form), DELIVERY_PLAN, formController),
                new OpenFormOption(getString(R.string.str_register_pnc_registration_form), DELIVERY_OUTCOME, formController),
                new OpenFormOption(getString(R.string.str_register_anc_investigations_form), ANC_INVESTIGATIONS, formController),
                new OpenFormOption(getString(R.string.str_register_anc_close_form), ANC_CLOSE, formController)
        };
    }

    @Override
    protected void onInitialization() {
        controller = new ANCSmartRegisterController(
                context.serviceProvidedService(),
                context.alertService(),
                context.allBeneficiaries(),
                context.listCache(),
                context.ancClientsCache());

        villageController = new VillageController(
                context.allEligibleCouples(),
                context.listCache(),
                context.villagesCache());

        dialogOptionMapper = new DialogOptionMapper();

        clientsProvider().onServiceModeSelected(new ANCOverviewServiceMode(clientsProvider()));
    }

    @Override
    protected void startRegistration() {
        FieldOverrides fieldOverrides = new FieldOverrides(context.anmLocationController().getLocationJSON());
        startFormActivity(AllConstants.FormNames.ANC_REGISTRATION_OA, null, fieldOverrides.getJSONString());
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    showProfileView((SmartRegisterClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(SmartRegisterClient client) {
            navigationController.startANC(client.entityId());
        }
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }
}

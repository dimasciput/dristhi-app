package org.ei.bidan.bidan.view.activity;

import android.view.View;

import org.ei.bidan.R;
import org.ei.bidan.adapter.SmartRegisterPaginatedAdapter;
import org.ei.bidan.bidan.provider.KBClientsProvider;
import org.ei.bidan.bidan.view.contract.KartuIbuClient;
import org.ei.bidan.bidan.view.controller.KohortKBRegisterController;
import org.ei.bidan.bidan.view.dialog.AllKBServiceMode;
import org.ei.bidan.bidan.view.dialog.NoIbuSort;
import org.ei.bidan.bidan.view.dialog.WifeAgeSort;
import org.ei.bidan.provider.SmartRegisterClientsProvider;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.ei.bidan.view.dialog.AllClientsFilter;
import org.ei.bidan.view.dialog.DialogOption;
import org.ei.bidan.view.dialog.DialogOptionMapper;
import org.ei.bidan.view.dialog.DialogOptionModel;
import org.ei.bidan.view.dialog.EditOption;
import org.ei.bidan.view.dialog.FilterOption;
import org.ei.bidan.view.dialog.NameSort;
import org.ei.bidan.view.dialog.ServiceModeOption;
import org.ei.bidan.view.dialog.SortOption;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class NativeKBSmartRegisterActivity extends BidanSecuredNativeSmartRegisterActivity {

    private SmartRegisterClientsProvider clientProvider = null;
    private KohortKBRegisterController controller;
    private DialogOptionMapper dialogOptionMapper;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
        };
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllKBServiceMode(clientsProvider());
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
                return getResources().getString(R.string.kb_register_title_in_short);
            }
        };
    }

    @Override
    public void setupViews() {
        super.setupViews();
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {

        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{new AllClientsFilter()};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new WifeAgeSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ki_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new KBClientsProvider(
                    this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        controller = new KohortKBRegisterController(context.allKartuIbus(),
                context.listCache(),context.kbClientsCache(),context.allKohort());
        dialogOptionMapper = new DialogOptionMapper();
    }

    @Override
    protected void startRegistration() {
        //FieldOverrides fieldOverrides = new FieldOverrides(context.anmLocationController().getLocationJSON());
        //startFormActivity(KARTU_IBU_REGISTRATION, null, fieldOverrides.getJSONString());
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout_ki:
                    showProfileView((KartuIbuClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(KartuIbuClient kartuIbuClient) {
            navigationController.startKI(kartuIbuClient.entityId());
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
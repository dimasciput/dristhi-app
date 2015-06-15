package org.ei.bidan.bidan.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import org.ei.bidan.AllConstants;
import org.ei.bidan.R;
import org.ei.bidan.adapter.SmartRegisterPaginatedAdapter;
import org.ei.bidan.bidan.provider.AnakRegisterClientsProvider;
import org.ei.bidan.bidan.view.contract.KartuIbuClient;
import org.ei.bidan.bidan.view.controller.AnakRegisterController;
import org.ei.bidan.bidan.view.dialog.AnakImmunizationServiceMode;
import org.ei.bidan.bidan.view.dialog.AnakOverviewServiceMode;
import org.ei.bidan.provider.SmartRegisterClientsProvider;
import org.ei.bidan.util.StringUtil;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.ei.bidan.view.dialog.AllClientsFilter;
import org.ei.bidan.view.dialog.DialogOption;
import org.ei.bidan.view.dialog.DialogOptionModel;
import org.ei.bidan.view.dialog.EditOption;
import org.ei.bidan.view.dialog.FilterOption;
import org.ei.bidan.view.dialog.NameSort;
import org.ei.bidan.view.dialog.OpenFormOption;
import org.ei.bidan.view.dialog.ReverseNameSort;
import org.ei.bidan.view.dialog.ServiceModeOption;
import org.ei.bidan.view.dialog.SortOption;

import java.util.Collections;
import java.util.List;

import static org.ei.bidan.AllConstants.FormNames.*;

/**
 * Created by Dimas Ciputra on 4/7/15.
 */
public class NativeKIAnakSmartRegisterActivity extends BidanSecuredNativeSmartRegisterActivity {

    private SmartRegisterClientsProvider clientProvider = null;
    private AnakRegisterController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if(clientProvider == null) {
            clientProvider = new AnakRegisterClientsProvider(this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AnakOverviewServiceMode(clientsProvider());
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
                return getResources().getString(R.string.child_register_title_in_short);
            }
        };
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
                return new DialogOption[]{new AnakOverviewServiceMode(clientsProvider()),
                new AnakImmunizationServiceMode(clientsProvider())};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new ReverseNameSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ki_search_hint);
            }
        };
    }

    @Override
    protected void onInitialization() {
        controller = new AnakRegisterController(
                context.allKohort(),
                context.alertService(),
                context.serviceProvidedService(),
                context.listCache(),
                context.smartRegisterClientsCache());

        clientsProvider().onServiceModeSelected(new AnakOverviewServiceMode(clientsProvider()));
    }
    @Override
    protected void startRegistration() {
        startFormActivity(AllConstants.FormNames.ANAK_NEW_REGISTRATION, null, null);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    // TODO : show info of timeline event
                    // showProfileView((ECClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
                case R.id.immunization_service_mode_views:
                    formController.startFormActivity(BAYI_IMUNISASI, "" + view.getTag(), null);
                    break;
            }
        }
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        String name;
        CharSequence listNames[];

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            SmartRegisterClient client = (SmartRegisterClient) tag;
            onShowDialogOptionSelection((EditOption)option, client, controller.getRandomNameChars(client));
        }
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_anak_neonatal),
                        BAYI_NEONATAL_PERIOD, formController),
                new OpenFormOption(getString(R.string.str_anak_bayi_visit),
                        KOHORT_BAYI_KUNJUNGAN, formController),
                new OpenFormOption(getString(R.string.str_anak_balita_visit),
                        BALITA_KUNJUNGAN, formController),
                new OpenFormOption(getString(R.string.str_imunisasi_bayi),
                        BAYI_IMUNISASI, formController),
                new OpenFormOption(getString(R.string.str_anak_edit),
                        KOHORT_BAYI_EDIT, formController),
                new OpenFormOption(getString(R.string.str_tutup_anak),
                        KARTU_IBU_ANAK_CLOSE, formController),
        };
    }
}

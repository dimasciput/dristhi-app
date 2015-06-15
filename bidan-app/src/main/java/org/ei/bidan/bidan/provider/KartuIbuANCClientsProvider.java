package org.ei.bidan.bidan.provider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.common.base.Strings;

import org.ei.bidan.R;
import org.ei.bidan.bidan.view.contract.KartuIbuANCClient;
import org.ei.bidan.bidan.view.controller.KartuIbuANCRegisterController;
import org.ei.bidan.bidan.view.viewHolder.NativeKIANCRegisterViewHolder;
import org.ei.bidan.provider.SmartRegisterClientsProvider;
import org.ei.bidan.view.activity.SecuredActivity;
import org.ei.bidan.view.contract.SmartRegisterClient;
import org.ei.bidan.view.contract.SmartRegisterClients;
import org.ei.bidan.view.dialog.FilterOption;
import org.ei.bidan.view.dialog.ServiceModeOption;
import org.ei.bidan.view.dialog.SortOption;
import org.ei.bidan.view.viewHolder.ECProfilePhotoLoader;
import org.ei.bidan.view.viewHolder.OnClickFormLauncher;
import org.ei.bidan.view.viewHolder.ProfilePhotoLoader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KartuIbuANCClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final SecuredActivity activity;
    private final View.OnClickListener onClickListener;
    private final ProfilePhotoLoader photoLoader;
    private final Context context;

    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected KartuIbuANCRegisterController controller;

    private Drawable iconPencilDrawable;

    public KartuIbuANCClientsProvider(
            SecuredActivity activity, View.OnClickListener onClickListener, KartuIbuANCRegisterController controller) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        photoLoader = new ECProfilePhotoLoader(activity.getResources(),
                activity.getResources().getDrawable(R.drawable.woman_placeholder));

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) activity.getResources().getDimension(R.dimen.list_item_height));

    }

    @Override
    public View getView(SmartRegisterClient client, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView;
        NativeKIANCRegisterViewHolder viewHolder;
        if (convertView == null) {
            itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_ki_anc_client, null);
            viewHolder = new NativeKIANCRegisterViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            itemView = (ViewGroup) convertView;
            viewHolder = (NativeKIANCRegisterViewHolder) itemView.getTag();
        }

        KartuIbuANCClient kartuIbuClient = (KartuIbuANCClient) client;
        setupClientProfileView(kartuIbuClient, viewHolder);
        setupIdDetailsView(kartuIbuClient, viewHolder);
        setupANCStatusView(kartuIbuClient, viewHolder);
        setupPemeriksaanView(kartuIbuClient, viewHolder);
        setupResikoView(kartuIbuClient, viewHolder);
        setupEditView(kartuIbuClient, viewHolder);
        setupEDDView(kartuIbuClient, viewHolder);

        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void setupEDDView(KartuIbuANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.edd().setText(client.eddForDisplay());
    }

    private void setupHighlightColor(KartuIbuANCClient client,ViewGroup itemView, int index) {
        if(index%2==0 && client !=null) {
            itemView.setBackgroundColor(Color.parseColor("#E0F5FF"));
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }

    private void setupClientProfileView(KartuIbuANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.profileInfoLayout().bindData(client, photoLoader);
        viewHolder.profileInfoLayout().setOnClickListener(onClickListener);
        viewHolder.profileInfoLayout().setTag(client);
    }

    private void setupIdDetailsView(KartuIbuANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.ancId().setText(client.kiNumber()==null?"-":client.kiNumber());
    }

    private void setupANCStatusView(KartuIbuANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.ancStatusHtpt().setText(client.tanggalHPHT()==null?"-":client.tanggalHPHT());
        viewHolder.ancStatusUsiaKlinis().setText(client.usiaKlinis()==null?"-":client.usiaKlinis());
    }

    private void setupPemeriksaanView(KartuIbuANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.getPemeriksaanBB().setText(client.getBeratBadan());
        viewHolder.getPemeriksaanLILA().setText(client.getLILA());
    }

    private void setupResikoView(KartuIbuANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        String penyakit = client.getPenyakitKronis() + "" +
                client.getRiwayatKomplikasiKebidanan() + "" + client.getHighRiskPregnancyReason() + "" +
                (!client.getAlergi().equalsIgnoreCase("NA") ? "(Alergi : "+client.getAlergi()+" )" : "");

        String alergi = client.getAlergi();

        viewHolder.getPenyakitKronis().setText(Strings.isNullOrEmpty(penyakit) ? "-" : penyakit);
        // viewHolder.getAlergi().setText(Strings.isNullOrEmpty(alergi) ? "-" : alergi);

        if(!Strings.isNullOrEmpty(penyakit) || !Strings.isNullOrEmpty(alergi)) {
            viewHolder.getLayoutResikoANC().setBackgroundColor(Color.parseColor("#FAD5D5"));
        } else {
            viewHolder.getLayoutResikoANC().setBackgroundColor(Color.parseColor("#CCFFCC"));
        }
    }

    private void setupEditView(KartuIbuANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.editButton().setImageDrawable(iconPencilDrawable);
        viewHolder.editButton().setOnClickListener(onClickListener);
        viewHolder.editButton().setTag(client);
    }


    @Override
    public SmartRegisterClients getClients() {
        return controller.getKartuIbuANCClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return new OnClickFormLauncher(activity, formName, entityId, metaData);
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}

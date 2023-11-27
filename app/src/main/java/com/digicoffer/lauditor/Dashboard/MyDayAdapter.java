package com.digicoffer.lauditor.Dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.ClientChatModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.EmailModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.Item;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.MeetingModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.NotificationModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.RelationshipRequestModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.TeamChatModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.ActiveModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.ApproxRevenueModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.AverageBillingRateModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.BillableModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.GroupsModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.HiringModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.NewClientsModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.NonBillableModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.PendingTimeSheetsModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.PracticeModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.RelationshipModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.StorageModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.SubScriptionModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.SubmittedTimesheetModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.TeamModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.TimeSheetModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.Constants;

import org.bouncycastle.util.Times;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> items;
    String mTag;

    public MyDayAdapter(ArrayList<Item> items, String tag) {
        this.items = items;
        mTag = tag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mTag == "MyDay_AAM") {
            //0=meeting_card
            if (viewType == 0) {
                return new MeetingHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.meeting_card,
                        parent,
                        false
                ));
            }//1=relationship_request_card
            //2=client_chat_card
            else if (viewType == 1) {
                return new ClientChatHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.client_chat_card,
                        parent,
                        false
                ));
            }//3=team_chat_card
            else if (viewType == 2) {
                return new TeamChatHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.team_chat_card,
                        parent,
                        false
                ));
            } else if (viewType == 3) {
                return new NotificationHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.notification_card,
                        parent,
                        false
                ));
            } else {
                return null;
            }
        } else if (mTag == "GH_KPI") {
            if (viewType == 0) {
                return new TBHholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_tbh, parent, false)
                );
            } else if (viewType == 1) {
                return new NBHholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_nbh, parent, false)
                );
            } else if (viewType == 2) {
                return new ARholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_approximate_revenue, parent, false)
                );
            } else if (viewType == 3) {
                return new ABRholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_average_billing_rate, parent, false)
                );
            } else if (viewType == 4) {
                return new TimeSheetholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_timesheet_card, parent, false)
                );
            } else if (viewType == 5) {
                return new Matterholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_matter_kpi, parent, false)
                );
            } else if (viewType == 6) {

                return new RelationshipRequestHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_client_relationships, parent, false)
                );
            } else if (viewType == 7) {
                return new USholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_used_storage_limit, parent, false)
                );
            } else {
                return null;
            }

        } else if (mTag == "SU_KPI") {
            if (viewType == 0) {
                return new TBHholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_tbh, parent, false)
                );
            } else if (viewType == 1) {
                return new NBHholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_nbh, parent, false)
                );
            } else if (viewType == 2) {
                return new ARholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_approximate_revenue, parent, false)
                );
            } else if (viewType == 3) {
                return new ABRholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_average_billing_rate, parent, false)
                );
            } else if (viewType == 4) {
                return new Matterholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_matter_kpi, parent, false)
                );
            } else if (viewType == 5) {
                return new NewClientsHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_kpi_new_client, parent, false)
                );
            } else if (viewType == 6) {
                return new NewHiresHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_kpi_new_hire, parent, false)
                );
            } else if (viewType == 7) {
                return new Data_storageHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_kpi_data_storage, parent, false)
                );
            } else {
                return null;
            }
        } else if (mTag == "TM_KPI") {
            if (viewType == 0) {
                return new TBHholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_tbh, parent, false)
                );
            } else if (viewType == 1) {
                return new NBHholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.practice_head_nbh, parent, false)
                );
            } else if (viewType == 2) {
                return new SubmittedTimeSheetholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.tm_submitted_timesheet, parent, false)
                );
            } else if (viewType == 3) {
                return new PendingTimeSheetholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.tm_submitted_timesheet, parent, false)
                );
            } else if (viewType == 4) {
                return new Matterholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_matter_kpi, parent, false)
                );
            }
        } else if (mTag == "Kpi_AAM") {
            if (viewType == 0) {
                return new MyDayAdapter.NOGHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.admin_no_of_groups, parent, false)
                );
            } else if (viewType == 1) {
                return new MyDayAdapter.TMholder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.admin_total_team_members, parent, false)
                );
            } else if (viewType == 2) {
                return new NewHiresHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_kpi_new_hire, parent, false)
                );
            } else if (viewType == 3) {
                return new Data_storageHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.super_user_kpi_data_storage, parent, false)
                );
            } else if (viewType == 4) {
                return new MyDayAdapter.ProductSubcriptionHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.admin_product_subscription, parent, false)
                );
            } else {
                return null;
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if(mTag=="MyDay_AAM") {
//                if (Constants.ROLE.equalsIgnoreCase("AAM")) {
                    if (getItemViewType(position) == 0) {
                        MeetingModel meetingModel = (MeetingModel) items.get(position).getObject();
                        ((MeetingHolder) holder).setMeetingData(meetingModel);
                    }
                    else if (getItemViewType(position) == 1) {
                        ClientChatModel clientChatModel = (ClientChatModel) items.get(position).getObject();
                        ((ClientChatHolder) holder).ClienChatData(clientChatModel);
                    }
                    else if (getItemViewType(position) == 2) {
                        TeamChatModel teamChatModel = (TeamChatModel) items.get(position).getObject();
                        ((TeamChatHolder) holder).TeamchatData(teamChatModel);
                    } else if (getItemViewType(position) == 3) {
                        NotificationModel notificationModel = (NotificationModel) items.get(position).getObject();
                        ((NotificationHolder) holder).setNotificationData(notificationModel);
                    }
            }else if(mTag == "GH_KPI"){
                if (getItemViewType(position) == 0) {
                    BillableModel billableModel = (BillableModel) items.get(position).getObject();
                    ((TBHholder) holder).TBHdata(billableModel);
                } else if (getItemViewType(position) == 1) {
                    NonBillableModel nonBillableModel = (NonBillableModel) items.get(position).getObject();
                    ((NBHholder) holder).NBHdata(nonBillableModel);
                } else if (getItemViewType(position) == 2) {
                    ApproxRevenueModel approxRevenueModel = (ApproxRevenueModel) items.get(position).getObject();
                    ((ARholder) holder).ARdata(approxRevenueModel);
                } else if (getItemViewType(position) == 3) {
                    AverageBillingRateModel averageBillingRateModel = (AverageBillingRateModel) items.get(position).getObject();
                    ((ABRholder) holder).ABRdata(averageBillingRateModel);
                } else if (getItemViewType(position) == 4) {
                    TimeSheetModel timeSheetModel = (TimeSheetModel) items.get(position).getObject();
                    ((TimeSheetholder) holder).TimeSheetdata(timeSheetModel);
                } else if (getItemViewType(position) == 5) {
                    ActiveModel activeModel = (ActiveModel) items.get(position).getObject();
                    ((Matterholder) holder).Mattersdata(activeModel);
                } else if (getItemViewType(position) == 6) {
                    RelationshipModel relationshipModel = (RelationshipModel) items.get(position).getObject();
                    ((RelationshipRequestHolder) holder).RequestsData(relationshipModel);
                } else if (getItemViewType(position)==7){
                    StorageModel uSholder = (StorageModel) items.get(position).getObject();
                    ((USholder) holder).USdata(uSholder);
                }
            }else if(mTag == "SU_KPI"){
                if (getItemViewType(position) == 0) {
                    BillableModel billableModel = (BillableModel) items.get(position).getObject();
                    ((TBHholder) holder).TBHdata(billableModel);
                } else if (getItemViewType(position) == 1) {
                    NonBillableModel nonBillableModel = (NonBillableModel) items.get(position).getObject();
                    ((NBHholder) holder).NBHdata(nonBillableModel);
                } else if (getItemViewType(position) == 2) {
                    ApproxRevenueModel approxRevenueModel = (ApproxRevenueModel) items.get(position).getObject();
                    ((ARholder) holder).ARdata(approxRevenueModel);
                } else if (getItemViewType(position) == 3) {
                    AverageBillingRateModel averageBillingRateModel = (AverageBillingRateModel) items.get(position).getObject();
                    ((ABRholder) holder).ABRdata(averageBillingRateModel);
                } else if (getItemViewType(position) == 4) {
                    ActiveModel activeModel = (ActiveModel) items.get(position).getObject();
                    ((Matterholder) holder).Mattersdata(activeModel);
                } else if (getItemViewType(position) == 5) {
                    NewClientsModel newClientsModel = (NewClientsModel) items.get(position).getObject();
                    ((NewClientsHolder) holder).setNewClientsdata(newClientsModel);
                } else if (getItemViewType(position) == 6) {
                    HiringModel hiringModel = (HiringModel) items.get(position).getObject();
                    ((NewHiresHolder) holder).setNewHiresdata(hiringModel);
                } else if (getItemViewType(position)==7){
                    StorageModel storageModel = (StorageModel) items.get(position).getObject();
                    ((Data_storageHolder) holder).setData_storageHolder(storageModel);
                }
            }else if(mTag == "TM_KPI"){

                if (getItemViewType(position) == 0) {
                    BillableModel billableModel = (BillableModel) items.get(position).getObject();
                    ((TBHholder) holder).TBHdata(billableModel);
                } else if (getItemViewType(position) == 1) {
                    NonBillableModel nonBillableModel = (NonBillableModel) items.get(position).getObject();
                    ((NBHholder) holder).NBHdata(nonBillableModel);
                } else if (getItemViewType(position) == 2) {
                    SubmittedTimesheetModel submittedTimesheetModel = (SubmittedTimesheetModel) items.get(position).getObject();
                    ((SubmittedTimeSheetholder) holder).SubmittedTimeSheetdata(submittedTimesheetModel);
                } else if (getItemViewType(position) == 3) {
                    PendingTimeSheetsModel pendingTimeSheetholder = (PendingTimeSheetsModel) items.get(position).getObject();
                    ((PendingTimeSheetholder) holder).PTSdata(pendingTimeSheetholder);
                } else if(getItemViewType(position)==4) {
                    ActiveModel activeModel = (ActiveModel) items.get(position).getObject();
                    ((ActiveMatterHolder) holder).Mattersdata(activeModel);
                }
            }else if(mTag=="Kpi_AAM"){
                if (getItemViewType(position) == 0) {
                    GroupsModel groupsModel = (GroupsModel) items.get(position).getObject();
                    ((NOGHolder) holder).setNOGData(groupsModel);
                } else if (getItemViewType(position) == 1) {
                    TeamModel teamModel = (TeamModel) items.get(position).getObject();
                    ((TMholder) holder).setTMdata(teamModel);
                } else if (getItemViewType(position) == 2) {
                    HiringModel hiringModel = (HiringModel) items.get(position).getObject();
                    ((NewHiresHolder) holder).setNewHiresdata(hiringModel);
                } else if (getItemViewType(position) == 3) {
                    StorageModel storageModel = (StorageModel) items.get(position).getObject();
                    ((Data_storageHolder) holder).setData_storageHolder(storageModel);
                }  else {
                    SubScriptionModel subScriptionModel = (SubScriptionModel) items.get(position).getObject();
                    ((ProductSubcriptionHolder) holder).setProductSubcriptiondata(subScriptionModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    static class MeetingHolder extends RecyclerView.ViewHolder {

        private TextView tv_from_ts, tv_to_ts, tv_subject;

        public MeetingHolder(@NonNull View itemView) {
            super(itemView);
            tv_from_ts = itemView.findViewById(R.id.tv_from_ts);
            tv_to_ts = itemView.findViewById(R.id.tv_to_ts);
            tv_subject = itemView.findViewById(R.id.tv_subject_meeting);
        }

        void setMeetingData(MeetingModel meetingData) {
            tv_from_ts.setText(meetingData.getFrom_ts());
            tv_to_ts.setText(meetingData.getTo_ts());
            tv_subject.setText(meetingData.getSubject());
        }
    }

    static class RelationshipRequestHolder extends RecyclerView.ViewHolder {
        private TextView tv_accepted_relationships, tv_pending_relationships,tv_deleted_timesheets;

        public RelationshipRequestHolder(@NonNull View itemView) {
            super(itemView);
            tv_accepted_relationships = itemView.findViewById(R.id.tv_accepted_relationships);
            tv_pending_relationships = itemView.findViewById(R.id.tv_pending_relationships);
            tv_deleted_timesheets = itemView.findViewById(R.id.tv_requested_time);
        }

        void RequestsData(RelationshipModel requestModel) {
            tv_accepted_relationships.setText(requestModel.getAccepted());
           tv_pending_relationships.setText(requestModel.getPending());
           tv_deleted_timesheets.setText("");
        }
    }

    static class ClientChatHolder extends RecyclerView.ViewHolder {
        private TextView tv_client_count, tv_chat_time, tv_client_chat_name, tv_client_message;

        public ClientChatHolder(@NonNull View itemView) {
            super(itemView);
//            tv_client_count = itemView.findViewById(R.id.tv_client_chat_count);
            tv_chat_time = itemView.findViewById(R.id.tv_client_chat_time);
            tv_client_chat_name = itemView.findViewById(R.id.tv_client_chat_name);
            tv_client_message = itemView.findViewById(R.id.tv_client_chat_message);
        }

        void ClienChatData(ClientChatModel clientChatModel) {
//            tv_client_count.setText(clientChatModel.getCount());
            tv_client_chat_name.setText(clientChatModel.getClient_name());
            tv_chat_time.setText(clientChatModel.getTime());
            tv_client_message.setText(clientChatModel.getChat_message());
        }
    }

    static class TeamChatHolder extends RecyclerView.ViewHolder {

        private TextView tv_tm_count, tv_tm_time, tv_tm_name, tv_tm_message;

        public TeamChatHolder(@NonNull View itemView) {
            super(itemView);
//            tv_tm_count = itemView.findViewById(R.id.tv_tm_count);
            tv_tm_name = itemView.findViewById(R.id.tv_tm_name);
            tv_tm_time = itemView.findViewById(R.id.tv_tm_time);
            tv_tm_message = itemView.findViewById(R.id.tv_tm_message);
        }

        void TeamchatData(TeamChatModel chatModel) {
//            tv_tm_count.setText(chatModel.getCount());
            tv_tm_time.setText(chatModel.getTime());
            tv_tm_name.setText(chatModel.getTm_name());
            tv_tm_message.setText(chatModel.getTm_message());
        }
    }

    static class EmailHolder extends RecyclerView.ViewHolder {
        TextView tv_email_count, tv_email_time, tv_email_subject, tv_email_message;

        public EmailHolder(@NonNull View itemView) {
            super(itemView);
            tv_email_count = itemView.findViewById(R.id.tv_email_count);
            tv_email_message = itemView.findViewById(R.id.tv_email_message);
            tv_email_subject = itemView.findViewById(R.id.tv_email_subject);
            tv_email_time = itemView.findViewById(R.id.tv_email_time);
        }

        void EmailData(EmailModel emailModel) {
            tv_email_time.setText(emailModel.getEmail_time());
            tv_email_count.setText(emailModel.getCount());
            tv_email_subject.setText(emailModel.getEmail_subject());
            tv_email_message.setText(emailModel.getEmail_message());
        }
    }

    static class TBHholder extends RecyclerView.ViewHolder{
        private  TextView tv_billing_hours,tv_bh_percentage;
        public TBHholder(@NonNull View itemView) {
            super(itemView);
            tv_billing_hours = itemView.findViewById(R.id.tv_bhours);
            tv_bh_percentage = itemView.findViewById(R.id.tv_bh_percentage);
        }

        void TBHdata(BillableModel billableModel){
            tv_billing_hours.setText(billableModel.getBillableHours());
            tv_bh_percentage.setText(billableModel.getBillablePercentage());

        }

    }

    static class NBHholder extends RecyclerView.ViewHolder{
        private  TextView tv_non_billing_hours,tv_nbh_percentage;
        public NBHholder(@NonNull View itemView) {
            super(itemView);
            tv_non_billing_hours = itemView.findViewById(R.id.tv_nb_hours);
            tv_nbh_percentage = itemView.findViewById(R.id.tv_nbh_percentage);
        }

        void NBHdata(NonBillableModel nonBillableModel){
            tv_non_billing_hours.setText(nonBillableModel.getNonBillableHours());
            tv_nbh_percentage.setText(nonBillableModel.getNonBillablePercentage());
        }
    }
    static class ARholder extends RecyclerView.ViewHolder{
        private TextView tv_revenue;
        public ARholder(@NonNull View itemView) {

            super(itemView);
            tv_revenue = itemView.findViewById(R.id.tv_revenue);
        }

        void ARdata(ApproxRevenueModel approxRevenueModel){
            tv_revenue.setText(approxRevenueModel.getApproxRevenue());
        }
    }
    static class ABRholder extends RecyclerView.ViewHolder{
        private TextView tv_billing_rate;
        public ABRholder(@NonNull View itemView) {
            super(itemView);
            tv_billing_rate = itemView.findViewById(R.id.tv_billing_rate);
        }

        void ABRdata(AverageBillingRateModel averageBillingRateModel){
            tv_billing_rate.setText(averageBillingRateModel.getAverageBillingRate());
        }
    }
    static class TimeSheetholder extends RecyclerView.ViewHolder{
        private TextView tv_ttm,tv_nsm,tv_sm;
        public TimeSheetholder(@NonNull View itemView) {
            super(itemView);
            tv_ttm = itemView.findViewById(R.id.tv_total_team_members_count);
            tv_nsm = itemView.findViewById(R.id.tv_not_submitted_count);
            tv_sm = itemView.findViewById(R.id.tv_submitted_members_count);
        }
        void TimeSheetdata(TimeSheetModel timeSheetModel){

            tv_ttm.setText("");
            tv_nsm.setText("");
            tv_sm.setText("");
//            tv_nsm.setText(timeSheetModel.getPending_start_date() +" to "+timeSheetModel.getPending_end_date());
//            tv_sm.setText(timeSheetModel.getSubmitted_start_date()+" to "+timeSheetModel.getSubmitted_end_date());

        }
    }
    static class Matterholder extends RecyclerView.ViewHolder{
        TextView tv_total_matters_count,tv_active_matters_count,tv_total_legal_matter_count,tv_active_legal_matter_count,tv_total_general_matter_count,tv_active_general_matter_count;
        public Matterholder(@NonNull View itemView) {
            super(itemView);
            tv_total_matters_count = itemView.findViewById(R.id.tv_total_matters_count);
            tv_active_matters_count = itemView.findViewById(R.id.tv_active_matters_count);
            tv_total_legal_matter_count = itemView.findViewById(R.id.tv_total_legal_matter_count);
            tv_active_legal_matter_count = itemView.findViewById(R.id.tv_active_legal_matter_count);
            tv_total_general_matter_count = itemView.findViewById(R.id.tv_total_general_matter_count);
            tv_active_general_matter_count = itemView.findViewById(R.id.tv_active_general_matter_count);
        }

        void Mattersdata(ActiveModel activeModel){
            tv_total_matters_count.setText(String.valueOf(activeModel.getTotal_closed()));
            tv_active_matters_count.setText(String.valueOf(activeModel.getTotal_active()));
            tv_total_legal_matter_count.setText(String.valueOf(activeModel.getClosed_legal_count())+" "+activeModel.getClosed_legal_type());
            tv_total_general_matter_count.setText(String.valueOf(activeModel.getClosed_general_count())+" "+activeModel.getClosed_general_type());
            tv_active_legal_matter_count.setText(String.valueOf(activeModel.getLegal_count())+" "+activeModel.getLegal_type());
            tv_active_general_matter_count.setText(String.valueOf(activeModel.getGeneral_count())+" "+activeModel.getGeneral_type());
        }
    }
    static class USholder extends RecyclerView.ViewHolder{
        private TextView tv_storage;
        public USholder(@NonNull View itemView) {
            super(itemView);
            tv_storage = itemView.findViewById(R.id.tv_storage);
        }

        void USdata(StorageModel storageModel){
            tv_storage.setText(storageModel.getCurrentStorage());
        }
    }
    static class CRholder extends RecyclerView.ViewHolder{
        private TextView tv_accepted_count,tv_pending_count,tv_deleted_count;
        public CRholder(@NonNull View itemView) {
            super(itemView);
            tv_accepted_count = itemView.findViewById(R.id.tv_accepted_count);
            tv_pending_count = itemView.findViewById(R.id.tv_pending_count);
            tv_deleted_count = itemView.findViewById(R.id.tv_delete_count);
        }

        void CRdata(PracticeModel practiceModel){
            tv_accepted_count.setText(practiceModel.getHours());
            tv_deleted_count.setText(practiceModel.getPercentage());
            tv_pending_count.setText(practiceModel.getSubmitted_members());
        }
    }
    static class NewClientsHolder extends RecyclerView.ViewHolder{
        private TextView tv_newclients_legal_count,tv_newclients_civil_count,tv_first_client_type,tv_second_client_type;
        public NewClientsHolder(@NonNull View itemView) {
            super(itemView);
            tv_newclients_legal_count = itemView.findViewById(R.id.tv_newclients_legal_count);
            tv_newclients_civil_count = itemView.findViewById(R.id.tv_newclients_civil_count);
            tv_first_client_type = itemView.findViewById(R.id.tv_first_client_type);
            tv_second_client_type = itemView.findViewById(R.id.tv_second_client_type);
        }

        void setNewClientsdata(NewClientsModel newClientsModel){
            tv_newclients_legal_count.setText(String.valueOf(newClientsModel.getCorporateCount()));
            tv_first_client_type.setText(newClientsModel.getCorporateType());
            tv_newclients_civil_count.setText(String.valueOf(newClientsModel.getCriminalCount()));
            tv_second_client_type.setText(newClientsModel.getCriminalType());

        }
    }
    static class NewHiresHolder extends RecyclerView.ViewHolder{
        private TextView tv_corporate_count,tv_criminal_count,tv_first_type,tv_second_type;
        public NewHiresHolder(@NonNull View itemView) {
            super(itemView);
            tv_corporate_count = itemView.findViewById(R.id.tv_newhire_corporate_count);
            tv_criminal_count = itemView.findViewById(R.id.tv_newhire_criminal_count);
            tv_first_type = itemView.findViewById(R.id.tv_first_type);
            tv_second_type = itemView.findViewById(R.id.tv_second_type);

        }

        void setNewHiresdata(HiringModel hiringModel){
            tv_corporate_count.setText(String.valueOf(hiringModel.getCorporateCount()));
            tv_first_type.setText(hiringModel.getCorporateType());
            tv_criminal_count.setText(String.valueOf(hiringModel.getCriminalCount()));
            tv_second_type.setText(hiringModel.getCriminalType());
        }
    }
    static class Data_storageHolder extends RecyclerView.ViewHolder{
        private TextView tv_data_storage,tv_balance_storage;
        public Data_storageHolder(@NonNull View itemView) {
            super(itemView);
            tv_data_storage = itemView.findViewById(R.id.tv_sp_data_storage);
            tv_balance_storage = itemView.findViewById(R.id.tv_sp_balance_storage);

        }

        void setData_storageHolder(StorageModel storageModel){
            tv_data_storage.setText(String.valueOf(storageModel.getTotalStorage()));
            tv_balance_storage.setText(String.valueOf(storageModel.getBalanceStorage()));

        }
    }
    static class SubmittedTimeSheetholder extends RecyclerView.ViewHolder{
        private TextView tv_from_date,tv_to_date;
        public SubmittedTimeSheetholder(@NonNull View itemView) {
            super(itemView);
            tv_from_date = itemView.findViewById(R.id.tv_from_date);
            tv_to_date = itemView.findViewById(R.id.tv_to_date);
        }
        void SubmittedTimeSheetdata(SubmittedTimesheetModel submittedTimesheetModel){

            tv_from_date.setText(submittedTimesheetModel.getStart_date());
            tv_to_date.setText(submittedTimesheetModel.getEnd_date());

        }
    }
    static class PendingTimeSheetholder extends RecyclerView.ViewHolder{
        private TextView tv_from_date,tv_to_date;
        public PendingTimeSheetholder(@NonNull View itemView) {
            super(itemView);
            tv_from_date = itemView.findViewById(R.id.tv_from_date);
            tv_to_date = itemView.findViewById(R.id.tv_to_date);
        }

        void PTSdata(PendingTimeSheetsModel pendingTimeSheetsModel){
            tv_from_date.setText(pendingTimeSheetsModel.getStart_date());
            tv_to_date.setText(pendingTimeSheetsModel.getEnd_date());
        }
    }
    static class ActiveMatterHolder extends RecyclerView.ViewHolder{
        private   TextView tv_general_count,tv_legal_count;
        public ActiveMatterHolder(@NonNull View itemView) {
            super(itemView);
            tv_general_count = itemView.findViewById(R.id.tv_general_count);
            tv_legal_count = itemView.findViewById(R.id.tv_legal_count);

        }

        void Mattersdata(ActiveModel activeModel){
            tv_general_count.setText(String.valueOf(activeModel.getGeneral_count()));
            tv_legal_count.setText(String.valueOf(activeModel.getLegal_count()));
        }
    }
    static class ProductSubcriptionHolder extends RecyclerView.ViewHolder{
        private TextView tv_ps_year;
        public ProductSubcriptionHolder(@NonNull View itemView) {
            super(itemView);
            tv_ps_year = itemView.findViewById(R.id.tv_ps_year);
        }
        void setProductSubcriptiondata(SubScriptionModel subScriptionModel){
            tv_ps_year.setText("");
        }
    }
    static class NOGHolder extends RecyclerView.ViewHolder{
        private TextView tv_nog_count;
        public NOGHolder(@NonNull View itemView) {
            super(itemView);
            tv_nog_count = itemView.findViewById(R.id.tv_nog_count);

        }

        void setNOGData(GroupsModel groupsModel){
            tv_nog_count.setText(String.valueOf(groupsModel.getTotalGroups()));


        }

    }
    static class TMholder extends RecyclerView.ViewHolder{
        private  TextView tv_admin_tm_count;
        public TMholder(@NonNull View itemView) {
            super(itemView);
            tv_admin_tm_count = itemView.findViewById(R.id.tv_admin_tm_count);
        }

        void setTMdata(TeamModel teamModel){
            tv_admin_tm_count.setText(String.valueOf(teamModel.getTotalTms()));
        }
    }

    private class NotificationHolder extends RecyclerView.ViewHolder {
        TextView tv_message,tv_timestamp,tv_date;
        public NotificationHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_dash_notification);
            tv_timestamp = itemView.findViewById(R.id.tv_notif_time);
            tv_date = itemView.findViewById(R.id.tv_notif_date);
        }

        void setNotificationData(NotificationModel notificationModel){
            tv_message.setText(notificationModel.getMessage());
            tv_date.setText(notificationModel.getDate());
            tv_timestamp.setText(notificationModel.getTimestamp());
        }
    }
}

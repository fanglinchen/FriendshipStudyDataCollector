package com.github.privacystreams.email;

import android.content.Context;
import android.os.AsyncTask;

import com.github.privacystreams.core.exceptions.PSException;
import com.github.privacystreams.core.providers.MStreamProvider;
import com.github.privacystreams.utils.TimeUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.*;

import android.Manifest;
import android.content.Intent;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *This is the provider that can query the time from a certain time period, which is for one time using.
 */

 class GmailProvider extends MStreamProvider implements GmailResultListener{
    private final String TAG = "GMAIL";
    static GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { GmailScopes.GMAIL_LABELS,GmailScopes.GMAIL_READONLY };
    private com.google.api.services.gmail.Gmail mService;
    private long lastTime = 0;
    private int  maxResult = 30;
    private long after = 0;
    private long before = 0;
    GmailProvider(long after,long before,int maxResult){
        this.after = after;
        this.before = before;
        this.maxResult = maxResult;
        this.addRequiredPermissions(Manifest.permission.INTERNET,Manifest.permission.GET_ACCOUNTS,Manifest.permission.ACCESS_NETWORK_STATE);
    }
    GmailProvider(){
        this.addRequiredPermissions(Manifest.permission.INTERNET,Manifest.permission.GET_ACCOUNTS,Manifest.permission.ACCESS_NETWORK_STATE);
    }
    @Override
    protected void provide() {
        getGmailInfo();
    }
    private void getGmailInfo(){
        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
            GmailActivity.setListener(this);
            Intent intent = new Intent(this.getContext(), GmailActivity.class);
            this.getContext().startActivity(intent);
    }
    @Override
    public void onSuccess() {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Gmail API Android Quickstart")
                .build();
        new MakeRequestTask().execute();
    }

    @Override
    public void onFail() {
        this.finish();
        this.raiseException(this.getUQI(), PSException.INTERRUPTED("Gmail canceled."));
    }

    private List<String> getDataFromApi(){
        List<String> messageList = new ArrayList<>();
        try{
            String query= buildTimeQuery(after,before);
            String user = "me";
            ListMessagesResponse response= mService.users().messages().list(user).setQ(query).execute();
            int total =1;
            String diliverTo = "";
            String from = "";
            String subject = "";
            String content = "";
            long timestamp = 0;
            long lastestTimeStamp = 0;
            if(response.getMessages()!=null){
                for(Message item : response.getMessages()){
                    if(total>maxResult){
                        break;
                    }
                    Message message = mService.users().messages().get("me",item.getId()).setFormat("full").execute();
                    List<MessagePart> messageParts = message.getPayload().getParts();
                    List<MessagePartHeader> headers = message.getPayload().getHeaders();

                    if(!headers.isEmpty()){
                        for(MessagePartHeader header:headers) {
                            String name = header.getName();
                            switch (name){
                                case "From":
                                    from = header.getValue();
                                    break;
                                case "To":
                                    diliverTo = header.getValue();
                                    break;
                                case "Subject":
                                    subject = header.getValue();
                                    break;
                                case "Date":
                                    String date = header.getValue();
                                    date = date.substring(date.indexOf(",")+2,date.length()-5);
                                    String timestampFormat = "dd MMM yyyy HH:mm:ss";
                                    timestamp = TimeUtils.fromFormattedString(timestampFormat,date);
                                    break;
                            }
                        }
                    }
                    if (messageParts!=null&&!messageParts.isEmpty()) {
                        byte[] bytes = Base64.decodeBase64(messageParts.get(0).getBody().getData());
                        if(bytes != null){
                            String mailText = new String(bytes);
                            if(!mailText.isEmpty()){
                                total++;
                                content = mailText;
                                messageList.add(mailText);
                            }
                        }
                    }
                    if(lastestTimeStamp==0) lastestTimeStamp = timestamp;
                    else if(lastestTimeStamp<timestamp) lastestTimeStamp = timestamp;
                    if(content!=null)
                        this.output(new Email(content,"Gmail",from,diliverTo,subject,timestamp));
                }
            }
            if(lastestTimeStamp!=0)
            lastTime =lastestTimeStamp;
            //Reset the value for from and to
            before = 0;
            after = 0;
        } catch (Exception e){
            e.printStackTrace();
        }
        return messageList;
    }
    private String buildTimeQuery(long after,long before){
        StringBuilder query = new StringBuilder("");
        query.append(" -category:{social promotions updates forums} ");
        if(after!=0){
            query.append("after:");
            query.append(after);
            query.append(" ");
        }
        if(before!=0){
            query.append("before:");
            query.append(before);
            query.append(" ");
        }
        return query.toString();
    }

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            return getDataFromApi();
        }
        @Override
        protected void onPostExecute(List<String> output) {
            Log.e("Test","end Asytn");
        }

    }

}

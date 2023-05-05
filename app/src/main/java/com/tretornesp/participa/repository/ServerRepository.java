package com.tretornesp.participa.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tretornesp.participa.BuildConfig;
import com.tretornesp.participa.model.request.LoginRequestModel;
import com.tretornesp.participa.repository.exception.BadRequestException;
import com.tretornesp.participa.repository.exception.LoginRequiredException;
import com.tretornesp.participa.repository.exception.RequestConformingException;
import com.tretornesp.participa.repository.exception.ResponseProcessingException;
import com.tretornesp.participa.repository.exception.ServerErrorException;
import com.tretornesp.participa.repository.exception.TokenExpiredException;
import com.tretornesp.participa.repository.exception.UnknownResponseCodeException;
import com.tretornesp.participa.util.SSLUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

public class ServerRepository {

    private static class InternalResponse {
        private final String body;
        private final int code;

        public InternalResponse(String body, int code) {
            this.body = body;
            this.code = code;
        }

        public String getBody() {
            return this.body;
        }

        public int getCode() {
            return this.code;
        }
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String loginUri = "/security/login";
    private static final String logoutUri = "/security/logout";
    private static final String validateUri = "/security/validateToken";
    private static final String refreshUri = "/security/refreshToken";
    private static final String verifyUri = "/security/isVerified";

    private static final String userUri = "/user/";

    private static final String proposalUri = "/proposal/";

    private static final String uploadUri = "/uploads/photo";

    enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        UPLOAD
    }

    public ServerRepository() {
    }

    private Request getRequest(String url, String token) {
        if (token == null || token.equals("")) {
            return new Request.Builder()
                    .url(url)
                    .build();
        } else {
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        }
    }
    private Request postRequest(String url, String token, String body) {
        if (token == null || token.equals("")) {
            return new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(body, JSON))
                    .build();
        } else {
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(RequestBody.create(body, JSON))
                    .build();
        }
    }
    private Request uploadRequest(String url, String token, File file) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/*")))
                .build();

        if (token == null || token.equals("")) {
            return new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else {
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();
        }
    }
    private Request putRequest(String url, String token, String body) {
        if (token == null || token.equals("")) {
            return new Request.Builder()
                    .url(url)
                    .put(RequestBody.create(body, JSON))
                    .build();
        } else {
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .put(RequestBody.create(body, JSON))
                    .build();
        }
    }
    private Request deleteRequest(String url, String token) {
        if (token == null || token.equals("")) {
            return new Request.Builder()
                    .url(url)
                    .delete()
                    .build();
        } else {
            return new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .delete()
                    .build();
        }
    }

    private InternalResponse parseResponse(Response response) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, IOException, JSONException {
        switch (response.code()) {
            case 200: {
                return new InternalResponse(response.body().string(), response.code());
            }
            case 201: {
                return new InternalResponse(response.body().string(), response.code());
            }
            case 400: {
                throw new BadRequestException("Bad request");
            }
            case 401: {
                String responseString = response.body().string();
                JSONObject object = new JSONObject(responseString);
                String error = object.getString("msg");
                if (error.equals("Token has expired")) {
                    throw new TokenExpiredException("Token expired");
                } else {
                    throw new LoginRequiredException("Login required");
                }
            }
            case 403: {
                throw new LoginRequiredException("Forbidden");
            }
            case 412: {
                return new InternalResponse(response.body().string(), response.code());
            }
            case 422: {
                throw new BadRequestException("Unsafe input detected");
            }
            case 500: {
                throw new ServerErrorException("Server error");
            }
            default:
                throw new UnknownResponseCodeException("Unknown response code");
        }
    }
    private InternalResponse act(Method method, String urlPart, String token, String body, File file) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        String baseUrl = BuildConfig.ROOT_URL + urlPart;

        Request request;
        switch (method) {
            case GET: {
                request = getRequest(baseUrl, token);
                break;
            }
            case POST: {
                request = postRequest(baseUrl, token, body);
                break;
            }
            case PUT: {
                request = putRequest(baseUrl, token, body);
                break;
            }
            case DELETE: {
                request = deleteRequest(baseUrl, token);
                break;
            }
            case UPLOAD: {
                request = uploadRequest(baseUrl, token, file);
                break;
            }
            default: {
                throw new RequestConformingException("Request method not supported");
            }
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return parseResponse(response);
        } catch (IOException ioe) {
            throw new ResponseProcessingException("[" + baseUrl + "]Got IOException parsing response: " + ioe.getMessage());
        } catch (JSONException e) {
            throw new ResponseProcessingException("Cant parse json response");
        }

    }

    //Security calls

    public String login(String loginData) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.POST, loginUri, null, loginData, null);
        return internalResponse.getBody();
    }
    public void logout(String token) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        act(Method.DELETE, logoutUri, token, null, null);
    }
    public boolean validateToken(String token) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.GET, validateUri, token, null, null);
        return internalResponse.getCode() == 200;
    }
    public String refreshToken(String token) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.GET, refreshUri, token, null, null);
        return internalResponse.getBody();
    }


    public boolean isVerified(String token) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.GET, verifyUri, token, null, null);
        return internalResponse.getCode() == 200;
    }

    //User calls
    public String getCurrentUser(String token) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.GET, userUri, token, null, null);
        return internalResponse.getBody();
    }

    public String getUser(String token, String uid) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.GET, userUri + uid, token, null, null);
        return internalResponse.getBody();
    }

    public String editUser(String token, String user) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.PUT, userUri, token, user, null);
        return internalResponse.getBody();
    }

    public void deleteUser(String token) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        act(Method.DELETE, userUri, token, null, null);
    }

    public String registerUser(String user) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        InternalResponse internalResponse = act(Method.POST, userUri, null, user, null);
        return internalResponse.getBody();
    }

    //Proposal calls

    public String getProposals(String token, String start, Integer size) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        String baseUrl;
        if (size == null) {
            baseUrl = proposalUri;
        } else {
            if (start == null || start.equals("-1")) {
                baseUrl = proposalUri + "?items=" + size;
            } else {
                baseUrl = proposalUri + "?start=" + start + "&items=" + size;
            }
        }

        InternalResponse internalResponse = act(Method.GET, baseUrl, token, null, null);
        return internalResponse.getBody();
    }

    public String getProposal(String token, String uid) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        InternalResponse internalResponse = act(Method.GET, proposalUri+uid, token, null, null);
        return internalResponse.getBody();
    }

    public String createProposal(String token, String proposalData) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        InternalResponse internalResponse = act(Method.POST, proposalUri, token, proposalData, null);
        return internalResponse.getBody();
    }

    public void deleteProposal(String token, String proposalId) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        act(Method.DELETE, proposalUri + proposalId, token, null, null);
    }

    public String editProposal(String token, String proposalId, String proposalData) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        InternalResponse internalResponse = act(Method.PUT, proposalUri + proposalId, token, proposalData, null);
        return internalResponse.getBody();
    }

    public String likeProposal(String token, String proposalId) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        InternalResponse internalResponse = act(Method.GET, proposalUri + "like?id=" + proposalId, token, null, null);
        return internalResponse.getBody();
    }

    public String unlikeProposal(String token, String proposalId) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException{
        InternalResponse internalResponse = act(Method.GET, proposalUri + "dislike?id=" + proposalId, token, null, null);
        return internalResponse.getBody();
    }

    //Upload calls

    public String uploadImage(String token, File image) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        InternalResponse internalResponse = act(Method.UPLOAD, uploadUri, token, null, image);
        return internalResponse.getBody();
    }

    public String presignImage(String token, String file) throws TokenExpiredException, BadRequestException, LoginRequiredException, UnknownResponseCodeException, ServerErrorException, ResponseProcessingException, RequestConformingException {
        String baseUrl = uploadUri + "/" + file;
        InternalResponse internalResponse = act(Method.GET, baseUrl, token, null, null);
        return internalResponse.getBody();
    }
}
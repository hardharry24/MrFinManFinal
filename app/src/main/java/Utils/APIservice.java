package Utils;

import Models.Biller.Biller;
import Models.Debts;
import Models.MyBill;
import Models.Result;
import Models.biller;
import Models.user;
import Singleton.UserLogin;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIservice {
    //UserDetails

    @GET("API_biller/getBillerInfo.php")
    Call<Biller> getBillerInfo(@Query("username") String username);

    @GET("API_biller/billerInfo.php")
    Call<biller> BillerInfo(@Query("username") String username);

    @GET("API_user/user_bill_list.php")
    Call<MyBill> mybills(@Query("userId") int userId);

    @FormUrlEncoded
    @POST("API_user/updateGoalisNotify.php")
    Call<Result> updateGoalisNotify(@Field("goalId") int goalId);

    @FormUrlEncoded
    @POST("API_user/updateBillisNotify.php")
    Call<Result> updateBillisNotify(@Field("billId") int billId);

    @FormUrlEncoded
    @POST("API_user/updateBillisNotifyBefore.php")
    Call<Result> updateBillisNotifyBefore(@Field("billId") int billId);

    @FormUrlEncoded
    @POST("API_user/updateDebtisNotify.php")
    Call<Result> updateDebtisNotify(@Field("debtId") int debtId);

    @FormUrlEncoded
    @POST("API_user/updateDebtBalance.php")
    Call<Result> updateDebtBalance(@Field("debtId") int debtId, @Field("balance") double amount);

    @GET("API_user/debt_list.php")
    Call<Debts> getUserDebts(@Query("userId") int userId);

    @FormUrlEncoded
    @POST("API_user/updateDebtisNotifyBefore.php")
    Call<Result> updateDebtisNotifyBefore(@Field("debtId") int debtId);

    @FormUrlEncoded
    @POST("API/updateUserInfo.php")
    Call<Result> updateUserInfo(@Field("usernameFrom") String usernameFrom,@Field("lname") String lname,
                                @Field("fname") String fname,@Field("mi") String mi,@Field("email") String email,
                                @Field("contact") String contact,@Field("username") String username,@Field("password") String password);

    @GET("API_user/UserDetails.php")
    Call<UserLogin> getUserDetails(@Query("username") String username);


    //Goal Check Method update

    @FormUrlEncoded
    @POST("API_user/updateGoalStatus.php")
    Call<Result> updateGoalStatus(@Field("goalId") int goalId);

    @FormUrlEncoded
    @POST("API_user/updateGoal.php")
    Call<Result> updateGoal(@Field("goalId") int goalId,@Field("todo") String todo,@Field("targetDate") String targetDate);
}

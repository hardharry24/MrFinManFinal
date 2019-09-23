// Generated code from Butter Knife. Do not modify!
package com.teamcipher.mrfinman.mrfinmanfinal.PopUp;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.teamcipher.mrfinman.mrfinmanfinal.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GoalPopUp_ViewBinding implements Unbinder {
  private GoalPopUp target;

  private View view2131361870;

  private View view2131361883;

  @UiThread
  public GoalPopUp_ViewBinding(GoalPopUp target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public GoalPopUp_ViewBinding(final GoalPopUp target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.btnAchieved, "method 'BtnAchieveOnclick'");
    view2131361870 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.BtnAchieveOnclick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnGoalUnachieved, "method 'btnUnAchievedOnclick'");
    view2131361883 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.btnUnAchievedOnclick(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    target = null;


    view2131361870.setOnClickListener(null);
    view2131361870 = null;
    view2131361883.setOnClickListener(null);
    view2131361883 = null;
  }
}

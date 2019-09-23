// Generated code from Butter Knife. Do not modify!
package com.teamcipher.mrfinman.mrfinmanfinal;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class Activity_profile_ViewBinding implements Unbinder {
  private Activity_profile target;

  private View view2131361878;

  private View view2131361888;

  @UiThread
  public Activity_profile_ViewBinding(Activity_profile target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public Activity_profile_ViewBinding(final Activity_profile target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.btnEdit, "field 'btnEditProfile' and method 'editProfile'");
    target.btnEditProfile = Utils.castView(view, R.id.btnEdit, "field 'btnEditProfile'", ImageView.class);
    view2131361878 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.editProfile(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnProfSave, "method 'saveChanges'");
    view2131361888 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.saveChanges(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    Activity_profile target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btnEditProfile = null;

    view2131361878.setOnClickListener(null);
    view2131361878 = null;
    view2131361888.setOnClickListener(null);
    view2131361888 = null;
  }
}

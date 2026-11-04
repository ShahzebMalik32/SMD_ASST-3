package com.example.taskflow;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/** Base class: gives every tab access to the shared project via the host callback. */
public abstract class ProjectFragment extends Fragment {

    protected ProjectHost host;

    protected ProjectFragment(int layoutRes) {
        super(layoutRes);
    }

    protected static Bundle argsFor(String projectId) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_PROJECT_ID, projectId);
        return args;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        host = (ProjectHost) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }

    /** Always the activity's current project object, never a private copy. */
    protected Project project() {
        return host.getProject();
    }

    /** Re-reads the project and redraws this tab. */
    public abstract void refresh();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && getView() != null && host != null) {
            refresh();
        }
    }
}

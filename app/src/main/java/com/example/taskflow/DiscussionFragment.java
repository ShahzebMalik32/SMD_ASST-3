package com.example.taskflow;

public class DiscussionFragment extends ProjectFragment {

    public DiscussionFragment() {
        super(R.layout.fragment_discussion);
    }

    public static DiscussionFragment newInstance(String projectId) {
        DiscussionFragment fragment = new DiscussionFragment();
        fragment.setArguments(argsFor(projectId));
        return fragment;
    }

    @Override
    public void refresh() {
        // Static "Coming Soon" screen, nothing to update.
    }
}

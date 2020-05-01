package com.example.lolbuild.mainApp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lolbuild.R;
import com.example.lolbuild.adapters.ChampionsAdapter;
import com.example.lolbuild.authentication.AuthenticationActivity;
import com.example.lolbuild.jobs.FetchChampions;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChampionsFragment extends Fragment implements FetchChampions.AsyncResponse {

    private static String championsJson;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    ChampionsAdapter championsAdapter;

    public ChampionsFragment() {
        // Required empty public constructor
    }

    public static String getChampionsJson() {
        return championsJson;
    }

    public static void setChampionsJson(String championsJson) {
        ChampionsFragment.championsJson = championsJson;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finishAndRemoveTask();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        FetchChampions fetchChampions = new FetchChampions();
        fetchChampions.setDelegate(this);
        fetchChampions.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        String email = auth.getCurrentUser().getEmail();
//        Log.i("User", auth.getCurrentUser().toString());
        recyclerView = view.findViewById(R.id.championsRecyclerView);
        gridLayoutManager = new GridLayoutManager(getContext(),5);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
    }

    @Override
    public void processFinish(String output) {
        if (output.equals("Success")) {
            championsAdapter = new ChampionsAdapter(getContext(), AuthenticationActivity.getChampions());
            recyclerView.setAdapter(championsAdapter);
        }
    }
}

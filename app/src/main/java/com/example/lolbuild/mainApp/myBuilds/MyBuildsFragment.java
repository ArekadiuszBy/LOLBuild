package com.example.lolbuild.mainApp.myBuilds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lolbuild.R;
import com.example.lolbuild.adapters.MyBuildsAdapter;
import com.example.lolbuild.jobs.AsyncResponse;
import com.example.lolbuild.jobs.FetchLolVersion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MyBuildsFragment extends Fragment implements AsyncResponse {
    private RecyclerView recyclerView;
    private FloatingActionButton createBuildFAB;
    private NavController navController;
    private static List<DocumentSnapshot> myBuilds;
    private ArrayList<String> savedBuildsIds;
    private String errorMessage;
    private FirebaseFirestore db;
    private String userID;

    public MyBuildsFragment() {

    }

    public static void setMyBuilds(List<DocumentSnapshot> myBuilds) {
        MyBuildsFragment.myBuilds = myBuilds;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        FetchLolVersion fetchLolVersion = new FetchLolVersion(sharedPreferences, this);
        fetchLolVersion.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DocumentReference userAccount = db.collection("accounts").document(userID);
        Task<DocumentSnapshot> task = userAccount.get();
        savedBuildsIds = null;
        errorMessage = null;
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        savedBuildsIds = (ArrayList<String>) task.getResult().get("savedBuilds");
                        if (savedBuildsIds.size() != 0) {
                            Query builds = db.collection("builds").whereIn(FieldPath.documentId(), savedBuildsIds);
                            Task<QuerySnapshot> querySnapshot = builds.get();
                            querySnapshot.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    myBuilds = querySnapshot.getResult().getDocuments();
                                    MyBuildsAdapter myBuildsAdapter = new MyBuildsAdapter(getContext(), myBuilds, false, true, userID, navController);
                                    recyclerView.setAdapter(myBuildsAdapter);
                                }
                            });
                        }
                    } else {
                        errorMessage = "You have no builds yet.";
                    }
                } else {
                    errorMessage = "Couldn't load the data.";
                }
            }
        });
        return inflater.inflate(R.layout.fragment_my_builds, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createBuildFAB = view.findViewById(R.id.createBuildFAB);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.myBuildsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
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

        createBuildFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_myBuildsFragment_to_championsFragment);
            }
        });

        if (errorMessage != null) {
            errorTextView.setText(errorMessage);
            errorTextView.setAlpha(1);
        }
    }

    @Override
    public void processFinish(String output) {
        if (output.equals("Success"))
            createBuildFAB.setClickable(true);
    }
}

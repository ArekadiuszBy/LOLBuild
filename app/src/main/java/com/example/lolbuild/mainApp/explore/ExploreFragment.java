package com.example.lolbuild.mainApp.explore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.lolbuild.R;
import com.example.lolbuild.adapters.MyBuildsAdapter;
import com.example.lolbuild.authentication.AuthenticationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    private static List<DocumentSnapshot> builds;
    private RecyclerView buildsRecyclerView;

    public ExploreFragment() {
        // Required empty public constructor
    }

    public static List<DocumentSnapshot> getBuilds() {
        return builds;
    }

    public static void setBuilds(List<DocumentSnapshot> builds) {
        ExploreFragment.builds = builds;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SearchView searchView = view.findViewById(R.id.searchView);
        buildsRecyclerView = view.findViewById(R.id.buildsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        buildsRecyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
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
        };

        buildsRecyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                builds = null;
                String searchQuery = query.substring(0, 1).toUpperCase() + query.substring(1).toLowerCase();
                Task<QuerySnapshot> task = db.collection("builds").whereEqualTo("champion", searchQuery).get();
                task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        builds = task.getResult().getDocuments();
                        MyBuildsAdapter myBuildsAdapter = new MyBuildsAdapter(getContext(), builds, true, false, false, user.getUid());
                        buildsRecyclerView.setAdapter(myBuildsAdapter);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
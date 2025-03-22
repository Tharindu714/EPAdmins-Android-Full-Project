package com.deltacodex.epadmins.ui.BlockUser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.deltacodex.epadmins.Adapter.SpaceItemDeco;
import com.deltacodex.epadmins.Adapter.UserAdapter;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class BlockUserFragment extends Fragment {
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_user, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1)); // 2 columns
        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        userAdapter = new UserAdapter(getContext(), userList, db);
        recyclerView.setAdapter(userAdapter);
        recyclerView.addItemDecoration(new SpaceItemDeco(25));

        loadUsers();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadUsers() {
        db.collection("Profile_user")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        userList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show());
    }
}

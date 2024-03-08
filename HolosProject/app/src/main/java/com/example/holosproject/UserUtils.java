package com.example.holosproject;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserUtils {

    /**
     * Checks if the current user is an admin.
     *
     * @param callback The callback to receive the admin check result.
     */
    public static void isAdminUser(FirebaseUser user, final AdminCheckCallback callback) {
        if (user == null) {
            callback.onCheckCompleted(false);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("userProfiles").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists() && "admin".equals(document.getString("role"))) {
                    callback.onCheckCompleted(true);
                } else {
                    callback.onCheckCompleted(false);
                }
            } else {
                Log.w("UserUtils", "Error checking admin status.", task.getException());
                callback.onCheckCompleted(false);
            }
        });
    }

    public interface AdminCheckCallback {
        void onCheckCompleted(boolean isAdmin);
    }
}

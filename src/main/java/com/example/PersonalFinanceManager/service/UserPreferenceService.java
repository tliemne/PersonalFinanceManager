    package com.example.PersonalFinanceManager.service;

    import com.example.PersonalFinanceManager.model.User;
    import com.example.PersonalFinanceManager.model.UserPreference;
    import com.example.PersonalFinanceManager.repository.UserPreferenceRepository;
    import com.example.PersonalFinanceManager.repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;

    @Service
    public class UserPreferenceService implements UserPreferenceServiceImpl {

        @Autowired
        private UserPreferenceRepository userPreferenceRepository;

        @Autowired
        private UserRepository userRepository;

        @Override
        public UserPreference createUserPreference(UserPreference userPreference) {
            return userPreferenceRepository.save(userPreference);
        }

        @Override
        public Optional<UserPreference> getUserPreferenceById(Long id) {
            return userPreferenceRepository.findById(id);
        }

        @Override
        public List<UserPreference> getAllUserPreferences() {
            return userPreferenceRepository.findAll();
        }

        @Override
        public UserPreference updateUserPreference(Long id, UserPreference userPreference) {
            return userPreferenceRepository.findById(id).map(e -> {
                e.setPreferredCurrency(userPreference.getPreferredCurrency());
                e.setTheme(userPreference.getTheme());
                e.setNotificationEnabled(userPreference.getNotificationEnabled());
                return userPreferenceRepository.save(e);
            }).orElseThrow(() -> new RuntimeException("UserPreference not found with id: " + id));
        }

        @Override
        public void deleteUserPreference(Long id) {
            if (!userPreferenceRepository.existsById(id)) {
                throw new RuntimeException("UserPreference not found with id: " + id);
            }
            userPreferenceRepository.deleteById(id);
        }

        // ✅ Sửa lại để tương thích với repository hiện tại
        public UserPreference getByUserId(Long userId) {
            UserPreference existingPref = userPreferenceRepository.findByUserId(userId);

            if (existingPref != null) {
                return existingPref;
            }

            // Nếu chưa có thì tạo mới
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            UserPreference pref = new UserPreference();
            pref.setUser(user);
            pref.setTheme(UserPreference.Theme.LIGHT);
            pref.setPreferredCurrency("VND");
            pref.setNotificationEnabled(true);

            return userPreferenceRepository.save(pref);
        }
    }

// Function to show or hide the password change section
// Function to toggle the visibility of the password change section
    function togglePasswordChange() {
        var section = document.getElementById('password-change-section');
        if (section.style.display === 'none' || section.style.display === '') {
            section.style.display = 'block';
        } else {
            section.style.display = 'none';
        }
    }

    // Function to confirm deletion of profile
    function confirmDelete() {
        return confirm("Apakah Anda yakin ingin menghapus akun Anda? Tindakan ini tidak dapat dibatalkan.");
    }
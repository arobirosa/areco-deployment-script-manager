return userService.getAdminUser().equals(userService.getCurrentUser()) ? "OK" : "This script must run as admin"

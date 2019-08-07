import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.user.UserService

def userService = Registry.getApplicationContext().getBean(UserService.class)

if (userService.getAdminUser().equals(userService.getCurrentUser())) {
    return "OK"
} else {
    return "The current user is " + userService.getCurrentUser().getUid()
}

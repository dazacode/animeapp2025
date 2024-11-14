package com.kawaidev.kawaime.ui.objects

object HelpMessages {
    const val ANIME_NOT_LOADED_TITLE = "Anime Not Loaded: Troubleshooting Steps"

    const val CHECK_INTERNET_CONNECTION = """
        <b>1. Check Your Internet Connection</b><br>
        - Make sure your device is connected to a stable Wi-Fi or mobile network.<br>
        - <b>For Wi-Fi:</b><br>
          - Check if you're connected to the right network.<br>
          - Try browsing a website or loading another app to confirm internet access.<br>
        - <b>For Mobile Data:</b><br>
          - Ensure mobile data is enabled in your device settings.<br>
          - Make sure you have sufficient signal strength or try switching between 4G/5G if available.<br><br>
        <b>How to check:</b><br>
        - <b>iOS:</b> Open "Settings" > "Wi-Fi" and check if you are connected.<br>
        - <b>Android:</b> Open "Settings" > "Network & Internet" and check your Wi-Fi or mobile data status.<br>
    """

    const val RESTART_APP = """
        <b>2. Restart the App</b><br>
        - Sometimes, simply restarting the app can resolve temporary loading issues. Close the app and reopen it to check if the issue persists.<br>
    """

    const val CLEAR_CACHE = """
        <b>3. Clear App Cache</b><br>
        - Cached data might cause issues if it becomes outdated or corrupted.<br>
        - <b>How to clear cache:</b><br>
          - <b>iOS:</b> Close the app and restart your device. Unfortunately, iOS does not offer direct cache clearing for apps, but restarting usually helps.<br>
          - <b>Android:</b><br>
            - Open **Settings** > **Apps** > [Your App Name] > **Storage** > **Clear Cache**.<br>
    """

    const val SERVER_ISSUES = """
        <b>4. Check for Server Issues</b><br>
        - It's possible that the app's servers are temporarily down or undergoing maintenance.<br>
        - You can check social media or the app's official support channels for any service status updates.<br>
    """

    const val UPDATE_APP = """
        <b>5. Update the App</b><br>
        - If you're using an older version of the app, it might have bugs or compatibility issues that could prevent data from loading.<br>
        - <b>How to update:</b><br>
          - <b>iOS:</b> Open the **App Store**, go to **Updates**, and update the app.<br>
          - <b>Android:</b> Open the **Google Play Store**, search for the app, and tap **Update** if available.<br>
    """

    const val CONTACT_SUPPORT = """
        <b>6. Contact Support</b><br>
        - If you've followed the steps above and the issue still persists, please reach out to our support team for further assistance.<br>
        - You can contact us via [email/website link] and provide details about the issue, including your device and app version.<br>
    """

    const val FINAL_MESSAGE = """
        By following these steps, you should be able to resolve the issue with the anime data not loading. If you continue to face issues, don’t hesitate to reach out for further help!<br>
    """
}
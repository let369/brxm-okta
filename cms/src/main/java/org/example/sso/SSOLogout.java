package org.example.sso;

import org.apache.wicket.request.flow.RedirectToUrlException;
import org.hippoecm.frontend.logout.CmsLogoutService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

public class SSOLogout extends CmsLogoutService {
    public SSOLogout(final IPluginContext context, final IPluginConfig config) {
        super(context, config);
    }

    @Override
    public void logout() {
        super.clearStates();
        super.logoutSession();
        redirectPage();
    }

    /**
     * Redirect it to (home)page
     */
    @Override
    protected void redirectPage() {
        throw new RedirectToUrlException("/logout.jsp");
    }
}

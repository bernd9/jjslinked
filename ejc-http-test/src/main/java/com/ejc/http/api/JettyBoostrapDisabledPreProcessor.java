package com.ejc.http.api;

import com.ejc.api.context.SingletonPreProcessor;

import java.util.Optional;

public class JettyBoostrapDisabledPreProcessor extends SingletonPreProcessor<JettyBootstrap> {

    public JettyBoostrapDisabledPreProcessor() {
        super(JettyBootstrap.class);
    }

    public Optional<JettyBootstrap> beforeInstantiation(Class<JettyBootstrap> type) {
        return Optional.of(new DisabledJettyBootstrap());
    }

    class DisabledJettyBootstrap extends JettyBootstrap {
    }
}

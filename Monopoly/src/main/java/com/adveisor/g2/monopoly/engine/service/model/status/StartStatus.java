/*
 * Copyright (c) ReichTUM 2022.
 */

package com.adveisor.g2.monopoly.engine.service.model.status;

import com.adveisor.g2.monopoly.engine.service.model.Game;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.TimerTask;

@JsonIgnoreProperties(ignoreUnknown=true)
public class StartStatus extends AbstractStatus {

    public StartStatus(Game game) {
        super(game);
        game.setCurrentStatusString("START");
    }
}

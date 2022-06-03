/*
 * Copyright (c) ReichTUM 2022.
 */

package com.adveisor.g2.monopoly.engine.service.model.player.status;

import com.adveisor.g2.monopoly.engine.service.model.player.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InJailStatus extends PlayerStatus{

    private int roundsInJail;

    public InJailStatus(Player player) {
        super(player);
        roundsInJail = 0;
    }

    @Override
    public boolean isInJail() {
        return true;
    }

    @Override
    public void moveForward(int steps) {
        if (++roundsInJail > 3) {
            buyOutOfJail();
            player.moveForward(steps);
        }
    }

    @Override
    public void setFree() {
        roundsInJail = 0;
        this.player.setCurrentStatus(player.getFreeStatus());
    }

    @Override
    public void handlePasch(boolean isPausch) {
        if (isPausch){
            this.roundsInJail = 0;
            setFree();
        }
    }

    @Override
    public void useJailCard() {
        int numJailCards = player.getNumJailCards();
        if (numJailCards > 0) {
            player.setNumJailCards(--numJailCards);
            setFree();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough jail cards");
        }
    }

    @Override
    public void buyOutOfJail() {
        player.adjustBalance(-50);
        setFree();
    }
}
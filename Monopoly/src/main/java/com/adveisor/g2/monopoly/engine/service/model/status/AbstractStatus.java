/*
 * Copyright (c) ReichTUM 2022.
 */

package com.adveisor.g2.monopoly.engine.service.model.status;

import com.adveisor.g2.monopoly.engine.service.model.Game;
import com.adveisor.g2.monopoly.engine.service.model.Piece;
import com.adveisor.g2.monopoly.engine.service.model.Player;
import com.adveisor.g2.monopoly.engine.service.model.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class AbstractStatus {

    protected Game game;

    AbstractStatus(Game game) {
        this.game = game;
    }

    // default method throw exception, overridden in concrete statuses
    public void join(String name) {
        throw new IllegalStateException("Failed to join game: already running.");
    }

    public void trade(ArrayList<String> offer, ArrayList<String> receive, int moneyOffer, int moneyReceive, int partnerId){
        throw new IllegalStateException("Can not trade while not being in TURN");
    }

    public void turn1(){
        int currentPlayer = game.getCurrentPlayer();
        Player player = game.getPlayers().get(currentPlayer);

        if(player.getBalance()<=0){
            Runnable r = new Runnable(){
                public void run(){
                    game.bankrupt(player);
                }
            };

            new Thread(r).start();
        }

        if(player.isBankrupt()==false && player.isPasch()==true){
            if(player.getNumPasch()==3) player.jail();
            else if (player.getNumPasch()<3) player.setNumPasch(player.getNumPasch()+1);
        }

        if(player.isPasch()==false){
            player.setNumPasch(0);
            while(player.isBankrupt()){
                currentPlayer = (currentPlayer +1)%game.getPlayers().size();
            }
        }

        //Abbruch falls ins Gefägnis gekommen
        if(player.isInJail()){
            game.setCurrentStatus(game.getJailStatus());
            return;
        }

        game.turn2();
    }

    public void sellBank(int fieldIndex){
        throw new IllegalStateException("Tried to sell despite not being in TURN");
    }

    public void buyHouse(int fieldIndex){
        throw new IllegalStateException("Can not buy house while not being in TURN");
    }

    public void sellHouse(int fieldIndex){
        throw new IllegalStateException("Can not sell house while not being in TURN");
    }

    public void endMortgage(int fieldIndex){
        throw new IllegalStateException("Cannot end mortgage while not being in TURN.");
    }

    public void startMortgage(int fieldIndex){
        throw new IllegalStateException("Can not start Mortgage while not being in TURN");
    }
    public void start(int timeLimit) {
        throw new IllegalStateException("Failed to start game: wrong status.");
    }
}

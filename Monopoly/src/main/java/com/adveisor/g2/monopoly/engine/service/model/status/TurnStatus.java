/*
 * Copyright (c) ReichTUM 2022.
 */

package com.adveisor.g2.monopoly.engine.service.model.status;

import com.adveisor.g2.monopoly.engine.service.model.Color;
import com.adveisor.g2.monopoly.engine.service.model.Field;
import com.adveisor.g2.monopoly.engine.service.model.Game;
import com.adveisor.g2.monopoly.engine.service.model.Player;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TurnStatus extends AbstractStatus {

    public TurnStatus(Game game) {
        super(game);
        game.setCurrentStatusString("TURN");
    }

    @Override
    public void sellBank(int fieldIndex){
        int currentPlayer = game.getCurrentPlayer();
        Player player = game.getPlayers().get(currentPlayer);
        Field field = game.getBoard().getFields().get(fieldIndex);
        if(!player.getPossession(fieldIndex)) throw new IllegalStateException("Tried to sell property not in possession");
        if(field.isHypothek()){
            player.endMortgage(fieldIndex);
            return;
        }
        if(field.getNumHouses()>0) return;
        player.adjustBalance(field.getPrice()/2);
        player.setPossession(fieldIndex, false);
        field.reset();
    }

    @Override
    public void buyHouse(int fieldIndex){
        int currentPlayer = game.getCurrentPlayer();
        Player player = game.getPlayers().get(currentPlayer);
        if(!player.getPossession(fieldIndex)) return;

        Field field = game.getBoard().getFields().get(fieldIndex);

        if(game.getNumHouses()<=0) return;
        if(field.getNumHouses()==4 && game.getNumHotels()<=0) return;
        if(field.isHypothek()) return;

        Color color = field.getColor();
        int minHouses = Integer.MAX_VALUE;
        // check if all the properties of a color are in players possession
        for(int i = 0; i<40; i++){
            Field running = game.getBoard().getFields().get(i);
            if(running.getColor()==color){
                if(running.getNumHouses()<minHouses) minHouses = running.getNumHouses();
                if(!player.getPossession(i)) return;
            }
        }

        if(field.getNumHouses()>minHouses) return;

        if(field.getNumHouses()==4){
            player.adjustBalance(-field.getHouseCost());
            field.setNumHouses(field.getNumHouses()+1);
            game.setNumHotels(game.getNumHotels()-1);
            game.setNumHouses(game.getNumHouses() +4);
        } else{
            player.adjustBalance(-field.getHouseCost());
            field.setNumHouses(field.getNumHouses()+1);
            game.setNumHouses(game.getNumHouses()-1);
        }
    }

    @Override
    public void sellHouse(int fieldIndex){
        if(game.getNumHouses()<=0) return;

        Player player = game.getPlayers().get(game.getCurrentPlayer());
        if(!player.getPossession(fieldIndex)) return;

        Field field = game.getBoard().getFields().get(fieldIndex);

        Color color = field.getColor();
        int maxHouses = Integer.MIN_VALUE;
        // check if all the properties of a color are in players possession
        for(int i = 0; i<40; i++){
            Field running = game.getBoard().getFields().get(i);
            if(running.getColor()==color){
                if(running.getNumHouses()>maxHouses) maxHouses = running.getNumHouses();
            }
        }

        if(field.getNumHouses()<maxHouses) return;

        if(field.getNumHouses()==5){
            if(game.getNumHouses()<4) return;
            player.adjustBalance(field.getHouseCost()/2);
            field.setNumHouses(field.getNumHouses()-1);
            game.setNumHouses(game.getNumHotels()+1);
            game.setNumHouses(game.getNumHouses() -4);
        } else{
            player.adjustBalance(field.getHouseCost()/2);
            field.setNumHouses(field.getNumHouses()-1);
            game.setNumHouses(game.getNumHouses()+1);
        }
    }

    @Override
    public void endMortgage(int fieldIndex){
        Player player = game.getPlayers().get(game.getCurrentPlayer());
        player.endMortgage(fieldIndex);
    }

    @Override
    public void startMortgage(int fieldIndex){
        Player player = game.getPlayers().get(game.getCurrentPlayer());
        player.startMortgage(fieldIndex);
    }

    @Override
    public void trade(ArrayList<String> offer, ArrayList<String> receive, int moneyOffer, int moneyReceive, int partnerId){
        int currentPlayer = game.getCurrentPlayer();
        Player player1 = game.getPlayers().get(currentPlayer);
        Player player2 = game.getPlayers().get(partnerId);
        int diff = moneyOffer - moneyReceive;
        player1.adjustBalance(-diff);
        player2.adjustBalance(diff);

        List<Field> field = game.getBoard().getFields();
        for(String street: offer){
            int index = game.getBoard().getIdFromString(street);
            field.get(index).setOwner(partnerId);
            player2.setPossession(index, true);
        }
        for(String street: receive){
            int index = game.getBoard().getIdFromString(street);
            field.get(index).setOwner(currentPlayer);
            player1.setPossession(index, true);
        }
    }
}

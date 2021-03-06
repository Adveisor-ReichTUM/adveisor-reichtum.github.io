package com.adveisor.g2.monopoly.controller;

import com.adveisor.g2.monopoly.engine.service.model.Game;
import com.adveisor.g2.monopoly.engine.service.model.Piece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@SpringBootApplication
public class GameController {
    @Autowired
    private static Game game = new Game("/text/board.txt", "/text/chanceDeck.txt", "/text/communityDeck.txt");

    // -----------------------
    @RequestMapping(value="/{file_name:.+}", method=RequestMethod.GET)
    public FileSystemResource getFrontend(@PathVariable("file_name") String file)
    {
        return new FileSystemResource("frontend/all-files/" + file);
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    public FileSystemResource get() {
        return getFrontend("loginpage_index.html");
    }

    // -------------------------

    @RequestMapping(value="/game", method = RequestMethod.GET, produces="application/json")
    public Game getGame(){
        return game;
    }

    @RequestMapping(value="/join", method = RequestMethod.GET, produces="application/json")
    public Game join(@RequestParam String name){
        game.join(name);
        return game;
    }

    @RequestMapping(value="/start", method=RequestMethod.GET, produces="application/json")
    public Game start(@RequestParam int timeLimit){
        game.start(timeLimit);
        return game;
    }

    @RequestMapping(value="/reset", method = RequestMethod.GET, produces="application/json")
    public Game reset(){
        game = new Game("setupfiles/board.txt", "setupfiles/chanceDeck.txt", "communityDeck.txt");
        return game;
    }

    @RequestMapping(value="/end", method = RequestMethod.GET, produces="application/json")
    public Game end(){
        game.end();
        return game;
    }

    @RequestMapping(value="/endturn", method = RequestMethod.GET, produces="application/json")
    public Game endTurn(){
        game.turn1();
        return game;
    }

    @RequestMapping(value="/jaildecision", method = RequestMethod.GET, produces="application/json")
    public Game decideJail(@RequestParam boolean choice){
        game.decideJail(choice);
        return game;
    }

    @RequestMapping(value="/jailcard", method = RequestMethod.GET, produces="application/json")
    public Game useJailCard(){
        game.useJailCard();
        return game;
    }

    @RequestMapping(value="/buyproperty", method = RequestMethod.GET, produces="application/json")
    public Game buy(){
        game.buy();
        return game;
    }

    @RequestMapping(value="/sellpropertybank", method = RequestMethod.GET, produces="application/json")
    public Game sellProp2Bank(@RequestParam int fieldIndex){
        game.sellBank(fieldIndex);
        return game;
    }

    @RequestMapping(value="/auction", method = RequestMethod.GET, produces="application/json")
    public Game passProperty(@RequestParam int fieldIndex){
        game.auctionProperty(fieldIndex);
        return game;
    }

    @RequestMapping(value="/trading", method = RequestMethod.GET, produces="application/json")
    public Game trade(@RequestParam ArrayList<String> offer, @RequestParam ArrayList<String> receive,
                      @RequestParam int moneyOffer, @RequestParam int moneyReceive, @RequestParam int partnerId){
        game.trade(offer, receive, moneyOffer, moneyReceive, partnerId);
        return game;
    }
    @RequestMapping(value="/bid", method = RequestMethod.GET, produces="application/json")
    public Game bid(@RequestParam String name, @RequestParam int bid){
        game.setBid(name, bid);
        return game;
    }

    @RequestMapping(value="/startmortgage", method = RequestMethod.GET, produces="application/json")
    public Game startMortgage(@RequestParam int fieldIndex){
        game.startMortgage(fieldIndex);
        return game;
    }

    @RequestMapping(value="/endmortgage", method = RequestMethod.GET, produces="application/json")
    public Game endMortgage(@RequestParam int fieldIndex){
        game.endMortgage(fieldIndex);
        return game;
    }

    @RequestMapping(value="/buyHouse", method = RequestMethod.GET, produces="application/json")
    public Game buyHouse(@RequestParam int fieldIndex){
        game.buyHouse(fieldIndex);
        return game;
    }

    @RequestMapping(value="/sellHouse", method = RequestMethod.GET, produces="application/json")
    public Game sellHouse(@RequestParam int fieldIndex){
        game.sellHouse(fieldIndex);
        return game;
    }

    @RequestMapping(value="/fieldsByPlayer", method = RequestMethod.GET, produces="application/json")
    public Game getFieldsByPlayer(){
        game.setTradeFields();
        return game;
    }

    @RequestMapping(value="/freecards", method = RequestMethod.GET, produces="application/json")
    public Game getFreeCards(){
        game.setFreeCards();
        return game;
    }

    public static void main(String[] args) throws Exception{
        SpringApplication.run(GameController.class, args);
        //game = new Game("/text/board.txt", "/text/chanceDeck.txt", "/text/communityDeck.txt");
    }
}

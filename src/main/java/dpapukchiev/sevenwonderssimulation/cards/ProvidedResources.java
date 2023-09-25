package dpapukchiev.sevenwonderssimulation.cards;

import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.wonder.CityName;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.cards.CardName.AKADEMIE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.AQUADUKT;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.ARENA;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.ARZNEIAUSGABE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.BELAGERUNGSMASCHINEN;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.BIBLIOTHEK;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.FORUM;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.GARTEN;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.GERICHT;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.HAFEN;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.KARAWANSEREI;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.LABORATORIUM;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.LEUCHTTURM;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.LOGE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.OBSERVATORIUM;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.PANTHENON;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.SCHIESSPLATZ;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.SENAT;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.STALLE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.STATUE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.STUDIENZIMMER;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.TEMPEL;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.UNIVERSTAT;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.VERTEIDIGUNGSANLAGE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.ZIRKUS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.GLASS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.TEXTILE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.CLAY;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.METAL_ORE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.STONE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.WOOD;

public class ProvidedResources {
    public boolean canBuildForFree(Card card, List<Card> builtCards) {
        return builtCards.stream()
                        .anyMatch(builtCard -> getFreeUpgrades(builtCard).contains(card.getName()));
    }
    public static Pair<List<RawMaterial>, List<ManufacturedGood>> getProvidedResources(CityName cityName) {
        return switch (cityName){
            case BABYLON -> Pair.of(List.of(CLAY), List.of());
            case RHODOS -> Pair.of(List.of(METAL_ORE), List.of());
            case OLIMPIA -> Pair.of(List.of(WOOD), List.of());
            case GIZAH -> Pair.of(List.of(STONE), List.of());
            case ALEXANDRIA -> Pair.of(List.of(), List.of(GLASS));
            case HALIKARNASSOS -> Pair.of(List.of(), List.of(TEXTILE));
            case EPHESOS -> Pair.of(List.of(), List.of(SCRIPTS));
        };
    }

    private List<CardName> getFreeUpgrades(Card builtCard) {
        return switch (builtCard.getName()) {
            case APOTEKE -> List.of(STALLE, ARZNEIAUSGABE);
            case ARZNEIAUSGABE -> List.of(ARENA, LOGE);
            case BADER -> List.of(AQUADUKT);
            case BIBLIOTHEK -> List.of(SENAT, UNIVERSTAT);
            case FORUM -> List.of(HAFEN);
            case KARAWANSEREI -> List.of(LEUCHTTURM);
            case KONTOR_OST, KONTOR_WEST -> List.of(FORUM);
            case LABORATORIUM -> List.of(BELAGERUNGSMASCHINEN, OBSERVATORIUM);
            case MARKT -> List.of(KARAWANSEREI);
            case MAUERN -> List.of(VERTEIDIGUNGSANLAGE);
            case SCHULE -> List.of(AKADEMIE, STUDIENZIMMER);
            case SKRIPTORIUM -> List.of(GERICHT, BIBLIOTHEK);
            case STATUE -> List.of(GARTEN);
            case TEMPEL -> List.of(PANTHENON);
            case THEATER -> List.of(STATUE);
            case TRAININGSGELANDE -> List.of(ZIRKUS);
            case WERKSTAT -> List.of(SCHIESSPLATZ, LABORATORIUM);
            case ALTAR -> List.of(TEMPEL);
            default -> List.of();
        };
    }
}

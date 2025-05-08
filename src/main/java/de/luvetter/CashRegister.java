package de.luvetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

// TODO: "Kasse" für Geld, dass nicht in Wechselgeld Stash passt
// TODO: Fremde Münzen / Objekte abweisen
public class CashRegister {

    private final Map<EuroCoin, CoinStash> stash = new EnumMap<>(EuroCoin.class);

    public CashRegister() {
        for (final EuroCoin coin : EuroCoin.values()) {
            stash.put(coin, new CoinStash(0));
        }
    }

    public EuroCoin[] getChange(final int price, final EuroCoin[] coins) {
        final int totalInserted = calculateTotalInserted(coins);
        validateInsertedCoversPrice(price, totalInserted);
        addCoins(coins);
        return getChange(totalInserted - price);
    }

    public void addCoins(final EuroCoin[] coins) {
        for (final EuroCoin coin : coins) {
            stash.get(coin).add(1);
        }
    }

    private int calculateTotalInserted(final EuroCoin[] coins) {
        return Stream.ofNullable(coins)
                       .flatMap(Arrays::stream)
                       .filter(Objects::nonNull)
                       .mapToInt(EuroCoin::getCents)
                       .sum();
    }

    private void validateInsertedCoversPrice(final int price, final int totalInserted) {
        if (price > 0 && totalInserted == 0) {
            throw new IllegalArgumentException("Bitte werfen Sie Geld ein");
        }
        if (totalInserted < price) {
            throw new IllegalArgumentException("Der gewählte Slot kostet " + price + " Cent, aber es wurden nur " + totalInserted + " Cent eingeworfen");
        }
    }

    // TODO: Findet aktuell nur in "einfachen" Fällen eine Lösung
    //      Erkennt z.b. nicht, das für 80 Cent 4x 20 Cent Münzen auch gehen, wenn eine 50 Cent Münze, aber keine 10 Cent Münze vorhanden ist
    // TODO: Eingeworfene Münzen wieder aus dem Stash entfernen, wenn kein Wechselgeld gegeben werden kann
    //      --> Hierfür die Münzen garnicht einzahlen, sondern erst schauen, ob davon welche "über" sind, also nicht für den Preis gebraucht werden
    //          und direkt abziehen. Von dem Rest dürfte dann nix als Wechselgeld über bleiben?
    private EuroCoin[] getChange(final int targetChangeSum) {
        int currentChangeSum = 0;
        final List<EuroCoin> change = new ArrayList<>();
        for (final EuroCoin coin : EuroCoin.values()) {
            final int remaining = targetChangeSum - currentChangeSum;
            final int maxPossibleCoins = remaining / coin.getCents();
            if (maxPossibleCoins == 0) {
                continue;
            }
            final int changeCoins = Math.min(stash.get(coin).getAmount(), maxPossibleCoins);
            stash.get(coin).remove(changeCoins);
            for (int i = 0; i < changeCoins; i++) {
                change.add(coin);
                currentChangeSum += coin.getCents();
            }
        }
        if (currentChangeSum != targetChangeSum) {
            // TODO: Schleife ohne die größte verfügbare Münzen wiederholen und erst Excpetion werfen, wenn man auch mit nur der kleinsten Münze nicht klar kommt.
            //          Würde das alle Fälle abdecken?
            throw new IllegalStateException("Nicht genug Wechselgeld im Automaten");
        }

        return change.toArray(new EuroCoin[0]);
    }

    public int emptyCoinType(final EuroCoin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("Bitte geben Sie eine Münze an");
        }
        return stash.get(coin).removeAll();
    }
}

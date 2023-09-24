package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;

public interface StrategyStep {
    enum Action {
        BUILD_WITH_COST,
        BUILD_FOR_FREE,
        BUILD_WITH_SPECIAL_EFFECT,
        DISCARD,
        WONDER,
        SKIP
    }

    record Result(Action action, Card card) {
        public static Result buildWithCost(Card card) {
            return new Result(Action.BUILD_WITH_COST, card);
        }

        public static Result buildForFree(Card card) {
            return new Result(Action.BUILD_FOR_FREE, card);
        }

        public static Result buildWithSpecialEffect(Card card) {
            return new Result(Action.BUILD_WITH_SPECIAL_EFFECT, card);
        }

        public static Result discard(Card card) {
            return new Result(Action.DISCARD, card);
        }

        public static Result wonder(Card card) {
            return new Result(Action.WONDER, card);
        }

        public static Result skip() {
            return new Result(Action.SKIP, null);
        }

    }

    Result execute(TurnContext turnContext);
}

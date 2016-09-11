module Quizz where

import Data.Function (fix)
import Control.Monad (join)

target :: [Integer]
-- target = take 5 $ fix (((<$>) <$> (:) <*> ((=<<) <$> (return <$>) <$> (*) <$> join (+))) 1)
target = take 5 entire

entire :: [Integer] -- powers of 2?..
-- entire = fix (((<$>) <$> (:) <*> ((=<<) <$> (return <$>) <$> (*) <$> join (+))) 1)
entire = fix (((<$>) <$> (:) <*> ((=<<) <$> (wtf) <$> (*) <$> join (+))) 1)
-- entire = fix fun

fun :: [Integer] -> [Integer] -- has fixed point --- list of powers of 2?..
-- fun = ((<$>) <$> (:) <*> ((=<<) <$> (return <$>) <$> (*) <$> join (+))) 1
fun = fun' 1

-- what if cut 1?
fun' :: Integer -> [Integer] -> [Integer]
-- fun' = (<$>) <$> (:) <*> ((=<<) <$> (return <$>) <$> (*) <$> join (+))
-- fun' = (<$>) <$> (:) <*> (fun'')
fun' = mgc <*> (fun'')

mgc :: a -> ([Integer] -> [a]) -> [Integer] -> [a]
mgc = (<$>) <$> (:)

fun'' :: Integer -> [Integer] -> [Integer]
-- fun'' = (=<<) <$> (return <$>) <$> (*) <$> join (+)
fun'' = (=<<) <$> (wtf) <$> (*) <$> join (+)

wtf :: (Integer -> a) -> Integer -> [a]
-- wtf = (return <$>)
wtf = (return .)



-- (=<<) :: Monad m => (a -> m b) -> m a -> m b  [infixr 1]
-- Same as >>=, but with the arguments interchanged.

-- (>>=) :: forall a b. m a -> (a -> m b) -> m b  [infixl 1]
-- Sequentially compose two actions, passing any value produced by the first as an argument to the second.

-- (<*>) :: f (a -> b) -> f a -> f b  [infixl 4]
-- Sequential application.

-- (<$>) :: Functor f => (a -> b) -> f a -> f b
-- An infix synonym for fmap.

-- join :: Monad m => m (m a) -> m a
-- The join function is the conventional monad join operator. It is used to remove one level of monadic structure, projecting its bound argument into the outer level.

-- return :: a -> m a
-- Inject a value into the monadic type.

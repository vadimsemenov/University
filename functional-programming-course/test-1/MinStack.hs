{-# LANGUAGE GADTs #-}

module MinStack where

data MinStack a where
  Empty    :: (Ord a) => MinStack a
  MinStack :: (Ord a) => [a] -> [a] -> MinStack a

push :: (Ord a) => MinStack a -> a -> MinStack a
push Empty e = MinStack [e] [e]
push (MinStack xs (m : ms)) e =
  MinStack (e : xs) ((if e <= m then e else m) : m : ms)
push _ _ = error "impossible"

pop :: MinStack a -> (a, MinStack a)
pop Empty                        = error "empty stack"
pop (MinStack (x : xs) (_ : ms)) = (x, if null xs then Empty else MinStack xs ms)
pop _                            = error "impossible"

minElem :: (Ord a) => MinStack a -> a
minElem Empty                = error "empty stack"
minElem (MinStack _ (m : _)) = m
minElem _                    = error "impossible"

isEmpty :: MinStack a -> Bool
isEmpty Empty = True
isEmpty _     = False

size :: MinStack a -> Int
size Empty           = 0
size (MinStack xs _) = length xs

multipop :: MinStack a -> Int -> ([a], MinStack a)
multipop Empty _ = ([], Empty)
multipop stack 0 = ([], stack)
multipop stack n = ((x : xs), stack')
  where
    (x, stack'') = pop stack
    (xs, stack') = multipop stack'' (n - 1)

module BinomialHeap ( BinomialHeap
                    , empty
                    , push
                    , pop
                    , getMin
                    , merge
                    ) where


type BinomialHeap a = [Node a]

empty :: BinomialHeap a
empty = []

push :: (Ord a) => BinomialHeap a -> a -> BinomialHeap a
push [] v = [alone v]
push bh v = compact (alone v : bh)

pop :: (Ord a) => BinomialHeap a -> (Maybe a, BinomialHeap a)
pop [] = (Nothing, [])
pop heaps = (Just minElement, newHeap)
  where
    (minElement, newHeap) = (val minNode, merge othersNode (children minNode))
    (minNode, othersNode, _) = foldr foldFun (undefined, [], []) heaps
    foldFun node (cm, hm, whole) = if null whole || val node <= val cm
                                   then (node, whole, node : whole)
                                   else (cm, node : hm, node : whole)

getMin :: (Ord a) => BinomialHeap a -> Maybe a
getMin [] = Nothing
getMin heads = Just $ val $ foldr1 (\a b -> if val a <= val b then a else b) heads

merge :: (Ord a) => BinomialHeap a -> BinomialHeap a -> BinomialHeap a
merge [] heap = heap
merge heap [] = heap
merge (f : fs) (s : ss)
  | degree f == degree s = merge (compact (unite f s : fs)) ss
  | degree f <  degree s = f : merge fs (s : ss)
  | otherwise            = s : merge (f : fs) ss

-- | Utility methods
data Node a = Node a Int [Node a] deriving Show

val :: Node a -> a
val (Node v _ _)      = v

degree :: Node a -> Int
degree (Node _ l _)    = l

children :: Node a -> [Node a]
children (Node _ _ c) = c

alone :: a -> Node a
alone v = Node v 1 []

compact :: (Ord a) => BinomialHeap a -> BinomialHeap a
compact [] = error "impossible"
compact [h] = [h]
compact (x : y : hs) = if degree x == degree y then compact $ unite x y : hs else x : y : hs

unite :: (Ord a) => Node a -> Node a -> Node a
unite a b = let (p, c) = if val a < val b then (a, b) else (b, a) in
  Node (val p) (1 + degree p) (c : children p)

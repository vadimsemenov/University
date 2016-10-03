{-# OPTIONS_GHC -fno-warn-orphans #-}

module BinarySearchTree
       ( find
       , insert
       , delete
       , merge
       , next
       , toList
       , fromList
       , Tree (..)
       ) where

import           TreePrinters (Tree (..))

find :: Ord a => Tree a -> a -> Bool
find Leaf _ = False
find (Node val left right) key
  | val == key = True
  | val < key  = find right key
  | otherwise  = find left key

insert :: Ord a => Tree a -> a -> Tree a
insert Leaf x = Node x Leaf Leaf
insert (Node val left right) x
  | val == x  = Node val left right
  | val < x   = Node val left (insert right x)
  | otherwise = Node val (insert left x) right

delete :: Ord a => Tree a -> a -> Tree a
delete Leaf _ = Leaf
delete (Node val left right) x
  | val == x  = merge left right
  | val < x   = Node val left (delete right x)
  | otherwise = Node val (delete left x) right

merge :: Ord a => Tree a -> Tree a -> Tree a
merge Leaf Leaf  = Leaf
merge Leaf tree  = tree
merge tree Leaf  = tree
merge left right = Node mx newLeft right
  where
    (mx, newLeft) = findMax left
    findMax :: Ord a => Tree a -> (a, Tree a)
    findMax Leaf              = error "could not happen"
    findMax (Node val l Leaf) = (val, l)
    findMax (Node val l r)    = let (m, nr) = findMax r in (m, Node val l nr)

toList :: Tree a -> [a]
toList =  foldr (:) []

fromList :: Ord a => [a] -> Tree a
fromList []       = Leaf
fromList (x : xs) = Node x (fromList $ filter (< x) xs) (fromList $ filter (> x) xs)

next :: Ord a => Tree a -> a -> Maybe a
next Leaf _ = Nothing
next (Node val left right) key
  | val == key = findMin right
  | val < key  = next right key
  | otherwise  = let m = next left key in case m of
                                            Nothing -> Just val
                                            _       -> m
  where
    findMin :: Ord a => Tree a -> Maybe a
    findMin Leaf            = Nothing
    findMin (Node v Leaf _) = Just v
    findMin (Node _ l _)    = findMin l

instance (Ord a) => Monoid (Tree a) where
  mempty = Leaf
  mappend = merge

instance Foldable Tree where
  foldr _ acc Leaf                  = acc
  foldr f acc (Node val left right) = foldr f (f val (foldr f acc right)) left

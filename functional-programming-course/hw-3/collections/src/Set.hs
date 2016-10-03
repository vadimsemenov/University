{-# LANGUAGE FlexibleInstances     #-}
{-# LANGUAGE MultiParamTypeClasses #-}

module Set
       ( Set (..)
       , Tree (..)
       ) where

import           BinarySearchTree (Tree (..))
import qualified BinarySearchTree as T

class (Ord a) => Set t a where
  empty    :: t a
  toList   :: t a -> [a]
  find     :: t a -> a -> Bool
  insert   :: t a -> a -> t a
  delete   :: t a -> a -> t a
  next     :: t a -> a -> Maybe a
  fromList :: [a] -> t a

instance (Ord a) => Set Tree a where
  empty = mempty
  toList = foldr (:) []
  find = T.find
  insert = T.insert
  delete = T.delete
  fromList = T.fromList
  next = T.next

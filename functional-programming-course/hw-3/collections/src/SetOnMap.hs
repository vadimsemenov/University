{-# LANGUAGE FlexibleContexts      #-}
{-# LANGUAGE FlexibleInstances     #-}
{-# LANGUAGE MultiParamTypeClasses #-}
{-# LANGUAGE UndecidableInstances  #-}

module SetOnMap (Set (..)) where

import           Data.Maybe (isJust)

import qualified Map        as M

class (M.Map t k k) => Set t k where
  empty    :: t (M.MapEntry k k)
  toList   :: t (M.MapEntry k k) -> [k]
  find     :: t (M.MapEntry k k) -> k -> Bool
  insert   :: t (M.MapEntry k k) -> k -> t (M.MapEntry k k)
  delete   :: t (M.MapEntry k k) -> k -> t (M.MapEntry k k)
  next     :: t (M.MapEntry k k) -> k -> Maybe k
  fromList :: [k] -> t (M.MapEntry k k)

instance (M.Map t k k) => Set t k where
  empty = M.empty
  find s key = isJust $ M.find s key
  insert s key = M.insert s key undefined
  delete = M.delete
  toList = map M.getKey . M.toList
  fromList = M.fromList . map (\k -> M.MapEntry k k)
  next s key = M.getKey <$> M.next s key

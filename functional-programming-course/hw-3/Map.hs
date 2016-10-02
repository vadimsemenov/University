{-# LANGUAGE FlexibleInstances     #-}
{-# LANGUAGE MultiParamTypeClasses #-}

module Map where

import qualified Set as S

class (Ord k) => Map t k v where
  empty    :: t (MapEntry k v)
  toList   :: t (MapEntry k v) -> [MapEntry k v]
  find     :: t (MapEntry k v) -> k -> Maybe v
  insert   :: t (MapEntry k v) -> k -> v -> t (MapEntry k v)
  delete   :: t (MapEntry k v) -> k -> t (MapEntry k v)
  next     :: t (MapEntry k v) -> k -> Maybe (MapEntry k v)
  fromList :: [MapEntry k v] -> t (MapEntry k v)

data MapEntry k v = MapEntry { getKey :: k, getValue :: v }

instance Eq k => Eq (MapEntry k v) where
  (==) x y = getKey x == getKey y

instance Ord k => Ord (MapEntry k v) where
  compare x y = compare (getKey x) (getKey y)

instance Ord k => Map S.Tree k v where
  empty              = S.empty
  toList             = S.toList
  fromList           = S.fromList
  insert m key value = S.insert m $ MapEntry key value
  delete m key       = S.delete m $ MapEntry key undefined
  next m key         = S.next m $ MapEntry key undefined
  find m key         = case m of
      S.Leaf               -> Nothing
      S.Node me left right -> case compare (getKey me) key of
          LT -> find right key
          GT -> find left key
          EQ -> Just $ getValue me

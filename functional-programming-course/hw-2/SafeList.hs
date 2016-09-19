module SafeList where

safeTail :: [a] -> Either String [a]
safeTail []      = Left "empty list"
safeTail (_ : t) = Right t

safeInit :: [a] -> Either String [a]
safeInit []   = Left "empty list"
safeInit list = Right $ safeInit' list
  where
    safeInit' []       = error "should not happen"
    safeInit' [_]      = []
    safeInit' (x : xs) = x : safeInit' xs

strip :: [a] -> Either String [a]
strip list = safeTail list >>= safeInit

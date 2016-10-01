module PhantomCoins where

newtype Coin color = Coin { getCoin :: Int }

instance Monoid (Coin color) where
  mempty                    = Coin 0
  mappend (Coin x) (Coin y) = Coin (x + y)

instance Num (Coin color) where
  (+)                   = mappend
  (*) (Coin x) (Coin y) = Coin (x * y) -- TODO: make point-free (*)
  abs                   = Coin . abs . getCoin
  signum                = Coin . signum . getCoin
  negate                = Coin . negate . getCoin
  fromInteger           = Coin . fromIntegral

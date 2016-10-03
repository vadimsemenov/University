module PhantomCoins where

newtype Coin color = Coin { getCoin :: Int }
  deriving Show

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

cmp :: (Estimated c1, Estimated c2) => Coin c1 -> Coin c2 -> Ordering
cmp x y = case compare (estimate x) (estimate y) of
  LT -> LT
  GT -> GT
  EQ -> compare (getCoin x) (getCoin y)

class Estimated a where
  estimate :: Coin a -> Double

data Blue
data Red

instance Estimated Blue where
  estimate _ = 1

instance Estimated Red where
  estimate _ = 2

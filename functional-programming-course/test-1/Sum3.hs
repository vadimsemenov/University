module Sum3 where

-- sum 3 lists
sum3 :: Num a => [a] -> [a] -> [a] -> [a]
sum3 [] [] []                   = []
sum3 [] [] xs                   = xs 
sum3 [] xs []                   = xs 
sum3 [] xs ys                   = sum3 xs ys []
sum3 xs [] ys                   = sum3 xs ys []
sum3 (x : xs) (y : ys) []       = (x + y) : (sum3 xs ys [])
sum3 (x : xs) (y : ys) (z : zs) = (x + y + z) : (sum3 xs ys zs)

import           BinomialHeap

import           Data.List

main :: IO ()
main = do
  let arr1 = [-5::Int, -2, -20, -1]
  let arr2 = [10::Int, 30 .. 1000]
  let heap = foldr (flip push) (foldl push empty arr1) arr2
  let unfold h = let (x, xs) = pop h in case x of
        Nothing -> []
        Just e  -> e : unfold xs
  print $ if unfold heap == sort (arr1 ++ arr2) then "passed" else "failed"

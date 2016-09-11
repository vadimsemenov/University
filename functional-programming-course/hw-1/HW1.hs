module HW1 (stringSum, zipN, mergeSort) where

import Data.Maybe (fromJust)
import Data.List  (unzip, uncons)
import Data.Char  (isDigit)


-- stringSum

stringSum :: String -> Int
stringSum str = sum $ map read' $ words str
  where
    cutFirst x = (head x == '+') && (isDigit $ head $ tail x)
    read' x = if cutFirst x then read $ tail x else read x


-- zipN

zipN :: ([a] -> b) -> [[a]] -> [b]
zipN fun lists = if null heads then [] else (fun heads) : (zipN fun tails)
  where (heads, tails) = unzip $ map (fromJust . uncons) $ takeWhile (not . null) lists


-- mergeSort

mergeSort :: Ord a => [a] -> [a]
mergeSort []   = []
mergeSort [a]  = [a]
mergeSort list = merge (mergeSort fs) (mergeSort ss)
  where (fs, ss) = split list

split :: [a] -> ([a], [a])
split []          = ([], [])
split [a]         = ([a], [])
split (a : b : t) = (a : fs, b : ss)
  where (fs, ss) = split t

merge :: Ord a => [a] -> [a] -> [a]
merge [] ss             = ss
merge fs []             = fs
merge (f : fs) (s : ss) = if f < s
                          then f : (merge fs (s : ss))
                          else s : (merge (f : fs) ss)

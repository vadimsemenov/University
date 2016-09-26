module Subsets where

subsets :: [a] -> Int -> [[a]]
subsets list n = filter ((== n) . length) $ if null list then [] else undefined

import qualified PhantomCoins as PC

main :: IO ()
main = do
  let c5b = PC.Coin 5 :: PC.Coin PC.Blue
  let c6b = PC.Coin 6 :: PC.Coin PC.Blue
  let c5r = PC.Coin 5 :: PC.Coin PC.Red
  let c4r = PC.Coin 4 :: PC.Coin PC.Red
  let correct = PC.cmp c5b c4r == LT
             && PC.cmp c5b c5b == EQ
             && PC.cmp c5r c6b == GT
             && PC.cmp c5r c4r == GT
             && PC.cmp c6b c4r == LT
             && PC.cmp c6b c5b == GT
  putStrLn $ if correct then "OK" else "Fail"

module GloriousBattle where

type Hp      = Integer
type Attack  = Integer
type Defence = Integer

data Result = LOSE | DRAW | WIN
  deriving Show

gloriousBattle :: Player -> [Monster] -> Result
gloriousBattle _ []       = WIN
gloriousBattle p (m : ms) = if draw p m then DRAW else case battle p m of
  Left res -> res
  Right pp -> case pp of
    Left ppp -> gloriousBattle ppp ms
    Right _  -> error "impossible"

draw :: (Entity a, Entity b) => a -> b -> Bool
draw x y = case punch x y of
  Right yy -> y == yy &&
               case punch y x of
                 Right xx -> x == xx
                 _        -> False
  _        -> False

battle :: (Entity a, Entity b) => a -> b -> Either Result (Either a b)
battle at en = case punch at en of
  Left eq     -> if isPlayer at then Right $ Left $ foldr improve at eq else Left LOSE
  Right newEn -> case battle newEn at of
    Left res -> Left res
    Right e  -> case e of
      Left r  -> Right $ Right r
      Right l -> Right $ Left l

data Equipment = Trant Hp | Rapira Attack | Shiva Defence
  deriving (Show, Eq)

class (Eq a) => Entity a where
  hp :: a -> Hp
  attack :: a -> Attack
  defence :: a -> Defence
  defend :: a -> Attack -> Either [Equipment] a
  isPlayer :: a -> Bool
  improve :: Equipment -> a -> a

punch :: (Entity a, Entity b) => a -> b -> Either [Equipment] b
punch at en = defend en (attack at)

data Player = Player Hp Attack Defence
  deriving (Show, Eq)
data Monster = Monster Hp Attack Defence [Equipment]
  deriving (Show, Eq)

instance Entity Player where
  hp (Player h _ _) = h
  attack (Player _ a _) = a
  defence (Player _ _ d) = d
  defend (Player h a d) att
    | att >= h + d = Left []
    | otherwise    = Right $ Player h - max 0 (att - d) a d
  isPlayer _ = True
  improve eq pl = case eq of
    Trant h  -> Player (h + hp pl) (attack pl) (defence pl)
    Rapira a -> Player (hp pl) (a + attack pl) (defence pl)
    Shiva d  -> Player (hp pl) (attack pl) (d + defence pl)

instance Entity Monster where
  hp (Monster h _ _ _) = h
  attack (Monster _ a _ _) = a
  defence (Monster _ _ d _) = d
  defend (Monster h a d items) att
    | att >= d + h = Left items
    | otherwise    = Right $ Monster h - max 0 (att - d) a d items
  isPlayer _ = False
  improve _ _ = error "cannot improve monster"

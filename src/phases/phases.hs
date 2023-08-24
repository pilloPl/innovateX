import Data.List (delete, partition)

-- Type definitions
data Etap = Etap String [Etap]
    deriving (Show, Eq)

data Faza = Faza [Etap]
    deriving Show

-- Function to create phases
stworzFazy :: [Etap] -> [Faza]
stworzFazy etapy = stworzFazyRekurencyjnie etapy []

stworzFazyRekurencyjnie :: [Etap] -> [Faza] -> [Faza]
stworzFazyRekurencyjnie [] fazy = fazy
stworzFazyRekurencyjnie etapy accumulatedFazy =
    let alreadyProcessedEtapy = concatMap (\(Faza es) -> es) accumulatedFazy
        (etapyBezZaleznosci, remainingEtapy) = partition (\e -> all (`elem` alreadyProcessedEtapy) (zaleznosci e)) etapy
    in if null etapyBezZaleznosci
       then accumulatedFazy
       else stworzFazyRekurencyjnie remainingEtapy (accumulatedFazy ++ [Faza etapyBezZaleznosci])

-- Helper function to extract dependencies from Etap
zaleznosci :: Etap -> [Etap]
zaleznosci (Etap _ deps) = deps

-- Main function for testing
main :: IO ()
main = do
    let etap1 = Etap "Etap1" []
        etap2 = Etap "Etap2" [etap1]
        etap3 = Etap "Etap3" [etap1]
        etap4 = Etap "Etap4" []
        etapy = [etap1, etap2, etap3, etap4]
        fazy = stworzFazy etapy
    mapM_ print fazy

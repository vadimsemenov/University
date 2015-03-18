CAT_DIR = cat/
REVWORDS_DIR = revwords/

all: cat/cat revwords/revwords

cat/cat: 
	$(MAKE) -C $(CAT_DIR)

revwords/revwords:
	$(MAKE) -C $(REVWORDS_DIR)

clean:
	$(MAKE) clean -C $(CAT_DIR)
	$(MAKE) clean -C $(REVWORDS_DIR)


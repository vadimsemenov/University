CAT_DIR = cat/
REVWORDS_DIR = revwords/
FILTER_DIR = filter/
BUFCAT_DIR = bufcat/
SIMPLESH_DIR = simplesh/
BIPIPER_DIR = bipiper/

all: cat/cat revwords/revwords filter/filter bufcat/bufcat simplesh/simplesh bipiper/forking bipiper/polling

$(BIPIPER_DIR)forking:
	$(MAKE) -C $(BIPIPER_DIR)

$(BIPIPER_DIR)polling:
	$(MAKE) -C $(BIPIPER_DIR)

$(SIMPLESH_DIR)simplesh:
	$(MAKE) -C $(SIMPLESH_DIR)

cat/cat: 
	$(MAKE) -C $(CAT_DIR)

revwords/revwords:
	$(MAKE) -C $(REVWORDS_DIR)

filter/filter:
	$(MAKE) -C $(FILTER_DIR)

bufcat/bufcat:
	$(MAKE) -C $(BUFCAT_DIR)

clean:
	$(MAKE) clean -C $(CAT_DIR)
	$(MAKE) clean -C $(REVWORDS_DIR)
	$(MAKE) clean -C $(FILTER_DIR)
	$(MAKE) clean -C $(BUFCAT_DIR)
	$(MAKE) clean -C $(SIMPLESH_DIR)
	$(MAKE) clean -C $(BIPIPER_DIR)

